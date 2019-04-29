package com.telus.apip.demo.sagaorchestration.serviceorder;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ServiceOrderController {
	
	private static final String SERVICE_ORDER_TOPIC_ARN = "arn:aws:sns:us-east-1:401620151136:MSA-Demo-SERVICE_ORDER";

	private Logger logger = LoggerFactory.getLogger(ServiceOrderController.class);
	
	private AmazonSNS snsClient;
	
	@Autowired
	private ServiceOrderService serviceOrderService;
	
	public ServiceOrderController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/serviceorder/event/customerorder", method = RequestMethod.POST, consumes = {"text/plain"})
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
		logger.debug("ServiceOrderController::handleCustomerOrderEvent AWS-Event: " + msg);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("ServiceOrderController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerOrderEvent coe = mapper.readValue(obj.get("Message").asText(), CustomerOrderEvent.class);
				logger.debug("ServiceOrderController::handleCustomerOrderEvent: " + coe.toString());
				
				//We can filter this out on the subscription configuration as well
				if (coe.getEventType().equals("CREATE_ORDER")) {
					logger.debug("ServiceOrderController::handleCustomerOrderEvent: Creating Service Order");
					ServiceOrder so = new ServiceOrder();
					so.setServiceOrderId(generateServiceOrderId());
					so.setCustomerOrderId(coe.getCustomerOrder().getCustomerOrderId());
					so.setOrderState("CREATED");
					for (ServiceLineItem s: coe.getCustomerOrder().getServiceList()) {
						so.addServiceItem(new ServiceItem(s.getServiceCode()));
					}
					serviceOrderService.save(so);
					
					ServiceOrderEvent soe = new ServiceOrderEvent(
						UUID.randomUUID().toString(), 	// Message ID
						coe.getCorrelationId(), 	// Correlation ID
						coe.getMessageId(), 		// Causation ID
						"SERVICE_ORDER_CREATE",
						so);
			
					try {
						String jsonInString = mapper.writeValueAsString(soe);
						logger.debug("EquipmentOrderController::handleCustomerOrderEvent::EquipmentOrderEvent:" + jsonInString);
						publishEvent(jsonInString);
					} catch (JsonProcessingException e) {
						logger.error("", e);
					}
					
				} else {
					logger.debug("ServiceOrderController::handleCustomerOrderEvent: Not a CREATE_ORDER event");
				}
			} else {
				logger.debug("ServiceOrderController::handleCustomerOrderEvent: Not a notification message");
			}
			
		} catch (IOException e) {
			logger.error("handleCustomerOrderEvent Exception", e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(SERVICE_ORDER_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the message.
		logger.debug("CourierController::publishEvent:Message: " + msg);
		logger.debug("CourierController::publishEvent:MessageId: " + publishResponse.getMessageId());
	}
	
	private String generateServiceOrderId() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}
	

}
