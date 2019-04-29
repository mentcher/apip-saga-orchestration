package com.telus.apip.demo.sagaorchestration.accounting;

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
public class AccountingController {
	
	private final static String CREDIT_CARD_AUTH_TOPIC_ARN = "";
	
	private Logger logger = LoggerFactory.getLogger(AccountingController.class);

	private AmazonSNS snsClient;
	
	@Autowired
	private CreditCardAuthReqService creditCardAuthReqService;

	public AccountingController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/accounting/event/customerorder", method = RequestMethod.POST, consumes = { "text/plain" })
	public HttpEntity<String> handleCustomerOrderEvent(@RequestBody String msg,
			@RequestHeader(value = "x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value = "x-amz-sns-message-id") String snsMsgId) {

		// If message doesn't have the message type header, don't process it.
		if (snsMsgType == null) {
			new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

		// Print the MessageId of the message.
		logger.debug("AccountingController::handleCustomerOrderEvent AWS-Event: " + msg);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("AccountingController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));
			
			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerOrderEvent coe = mapper.readValue(obj.get("Message").asText(), CustomerOrderEvent.class);
				logger.debug("CustomerController::handleCustomerOrderEvent: " + coe.toString());
				
				//We can filter this out on the subscription configuration as well
				if (coe.getEventType().equals("CREATE_ORDER")) {
					CreditCardAuthorizationRequest ccar = new CreditCardAuthorizationRequest(
							generateCreditCardAuthReqId(), 
							coe.getCustomerOrder().getCustomerId(), 
							coe.getCustomerOrder().getCustomerOrderId(), 
							0, //Pending 
							"100", //Amount should come from customer order
							generateRefNumber()); 
					creditCardAuthReqService.save(ccar);
				} else {
					logger.debug("CustomerController::handleCustomerOrderEvent: Not a CREATE_ORDER event");
				}
			} else {
				logger.debug("CustomerController::handleCustomerOrderEvent: Not a notification message");
			}
			

		} catch (IOException e) {
			logger.error("Error in handleCustomerOrderEvent",e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/accounting/event/customer", method = RequestMethod.POST, consumes = { "text/plain" })
	public HttpEntity<String> handleCustomerEvent(@RequestBody String msg,
			@RequestHeader(value = "x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value = "x-amz-sns-message-id") String snsMsgId) {

		// If message doesn't have the message type header, don't process it.
		if (snsMsgType == null) {
			new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

		// Print the MessageId of the message.
		logger.debug("AccountingController::handleCustomerOrderEvent AWS-Event: " + msg);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("AccountingController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));
			
			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerValidationEvent cve = mapper.readValue(obj.get("Message").asText(), CustomerValidationEvent.class);
				logger.debug("CustomerController::handleCustomerOrderEvent: " + cve.toString());
				
				if (cve.getEventType().equalsIgnoreCase("CUSTOMER_VALIDATION")) {
					//Lookup the request using the customer order id
					List<CreditCardAuthorizationRequest> ccarList = 
							creditCardAuthReqService.findByCustomerOrderId(cve.getCustomerOrderId());
					if (!ccarList.isEmpty()) {
						CreditCardAuthorizationRequest ccar = ccarList.get(0);
						ccar.updateStatusByEvent(CreditCardAuthorizationRequest.CredtCardAuthEvent.CustomerValidated);
						if (ccar.allEventsPassed()) {
							//Charge the card and send the event
							CreditCardAuthorizationEvent ccae = new CreditCardAuthorizationEvent();
							ccae.setMessageId(UUID.randomUUID().toString());
							ccae.setCorrelationId(cve.getCorrelationId());
							ccae.setCausationId(cve.getMessageId());
							ccae.setCreditCardAuthReq(ccar);
							try {
								String jsonInString = mapper.writeValueAsString(ccae);
								logger.debug("CustomerController::handleCustomerOrderEvent::CreditCardAuthorizationEvent:" + jsonInString);
								publishEvent(jsonInString);
							} catch (JsonProcessingException e) {
								logger.error("", e);
							}
						}
					} else {
						logger.debug(this.getClass().getSimpleName() + "::handleCustomerEvent: Customer Order Id not found [" + cve.getCustomerOrderId() + "]");
					}
				}
			} else {
				logger.debug(this.getClass().getSimpleName() + "::handleCustomerOrderEvent: Not a notification message");
			}
			

		} catch (IOException e) {
			logger.error("Error in handleCustomerOrderEvent",e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/accounting/event/serviceorder", method = RequestMethod.POST, consumes = { "text/plain" })
	public HttpEntity<String> handleServiceOrderEvent(@RequestBody String msg,
			@RequestHeader(value = "x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value = "x-amz-sns-message-id") String snsMsgId) {

		// If message doesn't have the message type header, don't process it.
		if (snsMsgType == null) {
			new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

		// Print the MessageId of the message.
		logger.debug(this.getClass().getSimpleName() + "::handleServiceOrderEvent AWS-Event: " + msg);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug(this.getClass().getSimpleName() + "::handleServiceOrderEvent AWS-Message: " + obj.get("Message"));
			
			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				ServiceOrderEvent soe = mapper.readValue(obj.get("Message").asText(), ServiceOrderEvent.class);
				logger.debug("CustomerController::handleServiceOrderEvent: " + soe.toString());
				
				if (soe.getEventType().equalsIgnoreCase("SERVICE_ORDER_CREATED")) {
					//Lookup the request using the customer order id
					List<CreditCardAuthorizationRequest> ccarList = 
							creditCardAuthReqService.findByCustomerOrderId(soe.getServiceOrder().getCustomerOrderId());
					if (!ccarList.isEmpty()) {
						CreditCardAuthorizationRequest ccar = ccarList.get(0);
						ccar.updateStatusByEvent(CreditCardAuthorizationRequest.CredtCardAuthEvent.ServiceOrderCreated);
						if (ccar.allEventsPassed()) {
							//Charge the card and send the event
							CreditCardAuthorizationEvent ccae = new CreditCardAuthorizationEvent();
							ccae.setMessageId(UUID.randomUUID().toString());
							ccae.setCorrelationId(soe.getCorrelationId());
							ccae.setCausationId(soe.getMessageId());
							ccae.setCreditCardAuthReq(ccar);
							try {
								String jsonInString = mapper.writeValueAsString(ccae);
								logger.debug("CustomerController::handleServiceOrderEvent::CreditCardAuthorizationEvent:" + jsonInString);
								publishEvent(jsonInString);
							} catch (JsonProcessingException e) {
								logger.error("", e);
							}
							
						}
					} else {
						logger.debug(this.getClass().getSimpleName() + "::handleCustomerEvent: Customer Order Id not found [" + soe.getServiceOrder().getCustomerOrderId() + "]");
					}
				}
			} else {
				logger.debug(this.getClass().getSimpleName() + "::handleCustomerOrderEvent: Not a notification message");
			}
		} catch (IOException e) {
			logger.error("Error in handleServiceOrderEvent",e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/accounting/event/equipmentorder", method = RequestMethod.POST, consumes = { "text/plain" })
	public HttpEntity<String> handleEquipmentOrderEvent(@RequestBody String msg,
			@RequestHeader(value = "x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value = "x-amz-sns-message-id") String snsMsgId) {

		// If message doesn't have the message type header, don't process it.
		if (snsMsgType == null) {
			new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

		// Print the MessageId of the message.
		logger.debug(this.getClass().getSimpleName() + "::handleEquipmentOrderEvent AWS-Event: " + msg);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug(this.getClass().getSimpleName() + "::handleEquipmentOrderEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				EquipmentOrderEvent soe = mapper.readValue(obj.get("Message").asText(), EquipmentOrderEvent.class);
				logger.debug("CustomerController::handleEquipmentOrderEvent: " + soe.toString());
				
				if (soe.getEventType().equalsIgnoreCase("EQUIPMENT_ORDER_CREATED")) {
					//Lookup the request using the customer order id
					List<CreditCardAuthorizationRequest> ccarList = 
							creditCardAuthReqService.findByCustomerOrderId(soe.getEquipmentOrder().getCustomerOrderId());
					if (!ccarList.isEmpty()) {
						CreditCardAuthorizationRequest ccar = ccarList.get(0);
						ccar.updateStatusByEvent(CreditCardAuthorizationRequest.CredtCardAuthEvent.EquipmentOrderCreated);
						if (ccar.allEventsPassed()) {
							//Charge the card and send the event
							CreditCardAuthorizationEvent ccae = new CreditCardAuthorizationEvent();
							ccae.setMessageId(UUID.randomUUID().toString());
							ccae.setCorrelationId(soe.getCorrelationId());
							ccae.setCausationId(soe.getMessageId());
							ccae.setCreditCardAuthReq(ccar);
							try {
								String jsonInString = mapper.writeValueAsString(ccae);
								logger.debug("CustomerController::handleEquipmentOrderEvent::CreditCardAuthorizationEvent:" + jsonInString);
								publishEvent(jsonInString);
							} catch (JsonProcessingException e) {
								logger.error("", e);
							}
						}
					} else {
						logger.debug(this.getClass().getSimpleName() + "::handleCustomerEvent: Customer Order Id not found [" + soe.getEquipmentOrder().getCustomerOrderId() + "]");
					}
				}
			} else {
				logger.debug(this.getClass().getSimpleName() + "::handleCustomerOrderEvent: Not a notification message");
			}
		} catch (IOException e) {
			logger.error("Error in handleEquipmentOrderEvent",e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	private String generateCreditCardAuthReqId() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}
	
	private String generateRefNumber() {
		return "TRXREF" + Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}

	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(CREDIT_CARD_AUTH_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the MessageId of the message.
		logger.debug(this.getClass().getSimpleName() + "::publishEvent:Message: " + msg);
		logger.debug(this.getClass().getSimpleName() + "::publishEvent:MessageId: " + publishResponse.getMessageId());
	}
	
	
	
}
