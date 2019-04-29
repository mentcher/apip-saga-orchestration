package com.telus.apip.demo.sagaorchestration.equiporder;

import java.io.IOException;
import java.util.List;
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
public class EquipmentOrderController {
	
	private static final String EQUIP_ORDER_TOPIC_ARN = "arn:aws:sns:us-east-1:401620151136:MSA-Demo-SERVICE_ORDER";

	private Logger logger = LoggerFactory.getLogger(EquipmentOrderController.class);
	
	private AmazonSNS snsClient;
	
	@Autowired
	private EquipmentOrderService equipOrderSvc;
	
	public EquipmentOrderController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/equipmentorder/event/customerorder", method = RequestMethod.POST, consumes = {"text/plain"})
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
		logger.debug("EquipmentOrderController::handleCustomerOrderEvent AWS-Event: " + msg);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("EquipmentOrderController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerOrderEvent coe = mapper.readValue(obj.get("Message").asText(), CustomerOrderEvent.class);
				logger.debug("EquipmentOrderController::handleCustomerOrderEvent: " + coe.toString());
				
				//We can filter this out on the subscription configuration as well
				if (coe.getEventType().equals("CREATE_ORDER")) {
					logger.debug("EquipmentOrderController::handleCustomerOrderEvent: Creating Equipment Order");
					EquipmentOrder eo = new EquipmentOrder();
					eo.setEqipmentOrderId(generateEquipmentOrderId());
					eo.setCustomerOrderId(coe.getCustomerOrder().getCustomerOrderId());
					eo.setCustomerId(coe.getCustomerOrder().getCustomerId());
					eo.setInstallType(coe.getCustomerOrder().getInstallType());
					eo.setOrderState("CREATED");
					for (EquipmentLineItem e : coe.getCustomerOrder().getEquipmentList()) {
						eo.addProductCode(e.getProductCode());
					}
					equipOrderSvc.save(eo);
					
					EquipmentOrderEvent eoe = new EquipmentOrderEvent(
							UUID.randomUUID().toString(), 	// Message ID
							coe.getCorrelationId(), 	// Correlation ID
							coe.getMessageId(), 		// Causation ID
							"EQUIPMENT_ORDER_CREATE",
							eo);
					
					try {
						String jsonInString = mapper.writeValueAsString(eoe);
						logger.debug("EquipmentOrderController::handleCustomerOrderEvent::EquipmentOrderEvent:" + jsonInString);
						publishEvent(jsonInString);
					} catch (JsonProcessingException e) {
						logger.error("", e);
					}
				} else {
					logger.debug("EquipmentOrderController::handleCustomerOrderEvent: Not a CREATE_ORDER event");
				}
			} else {
				logger.debug("EquipmentOrderController::handleCustomerOrderEvent: Not a notification message");
			}
			
		} catch (IOException e) {
			logger.error("handleCustomerOrderEvent Exception", e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/equipmentorder/event/courier", method = RequestMethod.POST, consumes = {"text/plain"})
	public HttpEntity<String> handleCourierEvent(
			@RequestBody String msg, 
			@RequestHeader(value="x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value="x-amz-sns-message-id") String snsMsgId
			) {
		
	    //If message doesn't have the message type header, don't process it.
        if (snsMsgType == null) {
        	new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
		
		// Print the message.
		logger.debug("EquipmentOrderController::handleCourierEvent AWS-Event: " + msg);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("EquipmentOrderController::handleCourierEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CourierRequestEvent ce = mapper.readValue(obj.get("Message").asText(), CourierRequestEvent.class);
				logger.debug("EquipmentOrderController::handleCourierEvent: " + ce.toString());
				//Update the equipment order with the courier waybill information
				List<EquipmentOrder> eolist = equipOrderSvc.findByCustomerOrderId(ce.getCourierRequest().getCustomerOrderId());
				if (!eolist.isEmpty()) {
					EquipmentOrder eo = eolist.get(0);
					eo.setCourierTrackingNumber(ce.getCourierRequest().getTrackingNumber());
					eo.setOrderState("PENDING");
				    equipOrderSvc.save(eo);
					EquipmentOrderEvent eoe = new EquipmentOrderEvent(
							UUID.randomUUID().toString(), 	// Message ID
							ce.getCorrelationId(), 	// Correlation ID
							ce.getMessageId(), 		// Causation ID
							"EQUIPMENT_ORDER_PENDING",
							eo);
					try {
						String jsonInString = mapper.writeValueAsString(eoe);
						logger.debug("EquipmentOrderController::handleCourierEvent::EquipmentOrderEvent:" + jsonInString);
						publishEvent(jsonInString);
					} catch (JsonProcessingException e) {
						logger.error("", e);
					}
				} else {
					logger.debug(
							"EquipmentOrderController::handleCourierEvent: Could not find order by customerOrderId [" + ce.getCourierRequest().getCustomerOrderId() +"]");
				}
				
				
			} else {
				logger.debug("EquipmentOrderController::handleCourierEvent: Not a notification message");
			}
			
		} catch (IOException e) {
			logger.error("handleCourierEvent Exception", e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}

	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(EQUIP_ORDER_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the message.
		logger.debug("CourierController::publishEvent:Message: " + msg);
		logger.debug("CourierController::publishEvent:MessageId: " + publishResponse.getMessageId());
	}
	
	private String generateEquipmentOrderId() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}
	

}
