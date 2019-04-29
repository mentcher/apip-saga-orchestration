package com.telus.apip.demo.sagaorchestration.customer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

	private Logger logger = LoggerFactory.getLogger(CustomerService.class);
	
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public CustomerService() {
	}
	
	public Customer get(String customerId) {
		Optional<Customer> coo = customerRepository.findById(customerId);
		if (coo.isPresent()) {
			return coo.get();
		} else {
			return null;
		}
	}
	
	public void save(Customer value) {
		customerRepository.save(value);
	}
	


}
