package com.telus.apip.demo.sagaorchestration.serviceorder;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceOrderService {
	
	@Autowired
	private ServiceOrderRepository serviceOrderRepository;
	
	public ServiceOrder get(String serviceOrderId) {
		Optional<ServiceOrder> soo = serviceOrderRepository.findById(serviceOrderId) ;
		if (soo.isPresent()) {
			return soo.get();
		} else {
			return null;
		}
	}
	
	public void save(ServiceOrder value) {
		serviceOrderRepository.save(value);
	}
	
	public List<ServiceOrder> findByCustomerOrderId(String customerOrderId){
		return serviceOrderRepository.findByCustomerOrderId(customerOrderId);
	}

}
