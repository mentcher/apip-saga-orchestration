package com.telus.apip.demo.sagaorchestration.courier;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourierRequestService {
	
	@Autowired
	private CourierRequestRepository repository;
	
	public CourierRequest get(String courierRequestId) {
		Optional<CourierRequest> coo = repository.findById(courierRequestId);
		if (coo.isPresent()) {
			return coo.get();
		} else {
			return null;
		}
	}
	
	public void add(CourierRequest value) {
		repository.save(value);
	}
	
	public List<CourierRequest> findByCustomerOrderId(String customerOrderId){
		return repository.findByCustomerOrderId(customerOrderId);
	}

}
