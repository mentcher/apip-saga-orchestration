package com.telus.apip.demo.sagaorchestration.accounting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CreditCardAuthReqRepository extends 
	CrudRepository<CreditCardAuthorizationRequest, String> {
	
	public List<CreditCardAuthorizationRequest> findByCustomerOrderId(@Param("customerOrderId") String customerOrderId);

}
