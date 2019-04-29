package com.telus.apip.demo.sagaorchestration.customer;

import java.io.IOException;
import java.util.UUID;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CustomerController {
	
	private Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private AmazonSNS snsClient;
	private static final String CUSTOMER_TOPIC_ARN = "arn:aws:sns:us-east-1:401620151136:MSA-Demo-CUSTOMER";
	
	@Autowired
	private CustomerService customerService;
	
	public CustomerController() {
		snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}
	
	@RequestMapping(value = "/customer/defaultload", method = RequestMethod.POST)
	public HttpEntity<?> loadDefaultCustomers(){
		
		try {
			customerService.save(new Customer("1", "Anne Bing","123 Any Street","4169998888", Customer.STATUS_ACTIVE));
			customerService.save(new Customer("2", "Jill St John","589 Richmond Ave","4168889999", Customer.STATUS_ACTIVE));
			customerService.save(new Customer("3", "Albert Killington","7816 Park Place","8057894422", Customer.STATUS_ACTIVE));
			customerService.save(new Customer("4", "Jacob Killington","7825 Park Place","8057892269", Customer.STATUS_DELINQUENT));
		} catch (Exception e) {
			logger.error("Error constructing CustomerService", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET )
	public HttpEntity<Customer> getCustomer(@PathVariable("id") String id){
		Customer c = customerService.get(id);
		if (c == null) {
			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Customer Not found [" + id + "]"); 
		} else {
			return new ResponseEntity<Customer>(c, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/customer/event/customerorder", method = RequestMethod.POST, consumes = {"text/plain"})
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
		logger.debug("CustomerController::handleCustomerOrderEvent AWS-Event: " + msg);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode obj = mapper.readTree(msg);
			logger.debug("CustomerController::handleCustomerOrderEvent AWS-Message: " + obj.get("Message"));

			if ("Notification".equalsIgnoreCase(snsMsgType)) {
				CustomerOrderEvent coe = mapper.readValue(obj.get("Message").asText(), CustomerOrderEvent.class);
				logger.debug("CustomerController::handleCustomerOrderEvent: " + coe.toString());
				
				//We can filter this out on the subscription configuration as well
				if (coe.getEventType().equals("CREATE_ORDER")) {
					//Check status of customer and publish to the CustomerValidationTopic
					CustomerValidationEvent cve = new CustomerValidationEvent();
					cve.setMessageId(UUID.randomUUID().toString()); //Generate a new message id
					cve.setCorrelationId(coe.getCorrelationId());    //Keep the existing correlation id
					cve.setCausationId(coe.getMessageId());          //Use the message id of the incoming message and the causation id
					
					//The customer order id will link this event for the
					//other transaction participants
					cve.setCustomerOrderId(coe.getCustomerOrder().getCustomerOrderId());
					cve.setEventType("CUSTOMER_VALIDATION");
					
					String custId = coe.getCustomerOrder().getCustomerId();
					Customer c = customerService.get(custId);
					if (c == null) {
						cve.setValidationStatus("FAILED");
					} else {
						cve.setCustomer(c);
						//Here just checks to see if the customer is active
						//Could add additional logic on the order itself, e.g. is the
						//order amount within their credit limit
						cve.setValidationStatus(
								c.getCustomerStatus().equals(Customer.STATUS_ACTIVE) ?
								"PASSED" : "FAILED");
					}
					
					String jsonInString = mapper.writeValueAsString(cve);
					logger.debug("CustomerController::handleCustomerOrderEvent:" + jsonInString);
					publishEvent(jsonInString);
				} else {
					logger.debug("CustomerController::handleCustomerOrderEvent: Not a CREATE_ORDER event");
				}
			} else {
				logger.debug("CustomerController::handleCustomerOrderEvent: Not a notification message");
			}
			
		} catch (IOException e) {
			logger.error("handleCustomerOrderEvent Exception", e);
		}
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	private void publishEvent(String msg) {
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(CUSTOMER_TOPIC_ARN, msg);
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the message.
		logger.debug("CustomerController::publishEvent:Message: " + msg);
		logger.debug("CustomerController::publishEvent:MessageId: " + publishResponse.getMessageId());
	}

}
