package com.telus.apip.demo.sagaorchestration.courier;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CourierRequestRepository extends CrudRepository<CourierRequest, String> {
	
	List<CourierRequest> findByCustomerOrderId(@Param("customerOrderId") String customerOrderId);

}
