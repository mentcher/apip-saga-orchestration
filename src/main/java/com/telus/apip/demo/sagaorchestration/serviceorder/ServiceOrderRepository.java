package com.telus.apip.demo.sagaorchestration.serviceorder;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ServiceOrderRepository extends CrudRepository<ServiceOrder, String> {
	
	List<ServiceOrder> findByCustomerOrderId(@Param ("customerOrderId") String customerOrderId);

}
