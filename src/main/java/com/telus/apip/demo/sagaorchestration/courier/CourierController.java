package com.telus.apip.demo.sagaorchestration.courier;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telus.apip.demo.sagaorchestration.customer.CustomerOrderEvent;

@RestController
public class CourierController {
	private static final String COURIER_TOPIC_ARN = "arn:aws:sns:us-east-1:401620151136:MSA-Demo-COURIER";

	private Logger logger = LoggerFactory.getLogger(CourierController.class);
	
	private AmazonSNS snsClient;
	
	@Autowired
	private CourierRequestService courierRequestService;
	
	public CourierController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/courier/event/customerorder", method = RequestMethod.POST, consumes = {"text/plain"})
	public HttpEntity<String> handleCustomerOrderEvent(
			@RequestBody String msg, 
			@RequestHeader(value="x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value="x-amz-sns-message-id") String snsMsgId
			) {
		
	    //If message doesn't have the message type header, don't process it.
        if (snsMsgType == null) {
        	new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
		
		// Print the message.
		logger.debug("CourierController::handleCustomerOrderEvent AWS-Event: " + msg);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("CourierController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerOrderEvent coe = mapper.readValue(obj.get("Message").asText(), CustomerOrderEvent.class);
				logger.debug("CourierController::handleCustomerOrderEvent: " + coe.toString());
				
				//We can filter this out on the subscription configuration as well
				if (coe.getEventType().equals("CREATE_ORDER")) {
					if (coe.getCustomerOrder().getInstallType().equalsIgnoreCase("SELF")) {
						//Initialize the courier request and save it
						//Finalize the request after the customer has been validated
						//Need to store the order information at this event since we won't have that
						//as part of the customer verification event
						CourierRequest cr = new CourierRequest(
								generateId(),
								coe.getCustomerOrder().getCustomerOrderId(),
								generateTrackingNumber(),
								coe.getCustomerOrder().getDeliveryAddress()); 
						courierRequestService.add(cr);
						
						CourierRequestEvent cre = new CourierRequestEvent();
						cre.setMessageId(UUID.randomUUID().toString()); //Generate a new message id
						cre.setCorrelationId(coe.getCorrelationId());    //Keep the existing correlation id
						cre.setCausationId(coe.getMessageId());          //Use the message id of the incoming message and the causation id
						cre.setEventType("COURIER_REQUEST_CREATED");
						cre.setCourierRequest(cr);
						
						String jsonInString = mapper.writeValueAsString(cre);
						logger.debug("CourierController::handleCustomerEvent:" + jsonInString);
						publishEvent(jsonInString);
						
					} else {
						logger.debug("CourierController::handleCustomerOrderEvent: Not a SELF install order");
					}
				} else {
					logger.debug("CourierController::handleCustomerOrderEvent: Not a CREATE_ORDER event");
				}
			} else {
				logger.debug("CourierController::handleCustomerOrderEvent: Not a notification message");
			}
			
		} catch (IOException e) {
			logger.error("handleCustomerOrderEvent Exception", e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}

	private String generateId() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}
	
	private String generateTrackingNumber() {
		return "TRK-" + Integer.toString(ThreadLocalRandom.current().nextInt(1, 100));
	}
	
	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(COURIER_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the message.
		logger.debug("CourierController::publishEvent:Message: " + msg);
		logger.debug("CourierController::publishEvent:MessageId: " + publishResponse.getMessageId());
	}

}
