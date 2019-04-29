package com.telus.apip.demo.sagaorchestration.customerorder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CustomerOrderController {
	private static final String CUSTOMER_ORDER_TOPIC_ARN = "arn:aws:sns:us-east-1:401620151136:MSA-Demo-ORDER";

	private Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);
	
	@Autowired
	private CustomerOrderService customerOrderService;

	private AmazonSNS snsClient;
	
	public CustomerOrderController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/customerorder/mock", method = RequestMethod.GET)
	public HttpEntity<CustomerOrder> getMockCustomerOrder() {
		CustomerOrder co = new CustomerOrder();
		co.setCustomerOrderId(generateCustomerOrderId());
		co.setCustomerId("1111");
		co.setCustomerOrderStatus("NEW");
		co.addEquipmentItem(new EquipmentLineItem("N001", new BigDecimal(12.99)));
		co.addEquipmentItem(new EquipmentLineItem("N002", new BigDecimal(15.99)));
		co.addServiceItem(new ServiceLineItem("SC001", new BigDecimal(3.99)));
		co.addServiceItem(new ServiceLineItem("SC002", new BigDecimal(7.99)));

		return new ResponseEntity<>(co, HttpStatus.OK); 
	}
	
	
	@RequestMapping(value = "/customerorder/{id}", method = RequestMethod.GET)
	public HttpEntity<CustomerOrder> getCustomerOrder(@PathVariable("id") String id) {
		CustomerOrder co = customerOrderService.get(id);
		if (co != null) {
			return new ResponseEntity<>(co, HttpStatus.OK); 
		} else {
			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Customer Order Not found [" + id + "]"); 
		}
	}
	
	@RequestMapping(value = "/customerorder", method = RequestMethod.POST)
	public HttpEntity<CustomerOrder> addCustomerOrder(@RequestBody CustomerOrder anOrder) {
		anOrder.setCustomerOrderId(generateCustomerOrderId());
		customerOrderService.add(anOrder);
		
		CustomerOrderEvent oe = new CustomerOrderEvent(
				UUID.randomUUID().toString(), 	// Message ID
				UUID.randomUUID().toString(), 	// Correlation ID
				"0", 							// Causation ID
				"CREATE_ORDER", 				// Event Type
				anOrder);

		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = mapper.writeValueAsString(oe);
			logger.debug("CustomerOrderController::addCustomerOrder::CustomerOrderEvent:" + jsonInString);
			publishEvent(jsonInString);
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
		
		return new ResponseEntity<>(anOrder, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/customerorder/event/creditcard", method = RequestMethod.POST, consumes = { "text/plain" })
	public HttpEntity<String> handleCreditCardEvent(@RequestBody String msg,
			@RequestHeader(value = "x-amz-sns-message-type") String snsMsgType,
			@RequestHeader(value = "x-amz-sns-message-id") String snsMsgId) {

		// If message doesn't have the message type header, don't process it.
		if (snsMsgType == null) {
			new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

		// Print the message.
		logger.debug("CustomerOrderController::handleCreditCardEvent AWS-Event: " + msg);

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("CustomerOrderController::handleCreditCardEvent AWS-Message: " + obj.get("Message"));
		} catch (IOException e) {
			logger.error("Error in handleCreditCardEvent",e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	private String generateCustomerOrderId() {
		return Integer.toString(ThreadLocalRandom.current().nextInt(1, 10000));
	}

	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(CUSTOMER_ORDER_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the MessageId of the message.
		logger.debug("CustomerOrderController::publishEvent:Message: " + msg);
		logger.debug("CustomerOrderController::publishEvent:MessageId: " + publishResponse.getMessageId());
	}
	
}
