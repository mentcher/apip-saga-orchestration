package com.telus.apip.demo.sagaorchestration.customerorder;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerOrderService {

	@Autowired
	private CustomerOrderRepository repository;
	
	public CustomerOrder get(String customerOrderId) {
		Optional<CustomerOrder> coo = repository.findById(customerOrderId);
		if (coo.isPresent()) {
			return coo.get();
		} else {
			return null;
		}
	}
	
	public void add(CustomerOrder value) {
		repository.save(value);
	}
}
