package com.telus.apip.demo.sagaorchestration.accounting;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditCardAuthReqService {
	
	@Autowired
	private CreditCardAuthReqRepository creditCardAuthReqRepository;
	
	public CreditCardAuthorizationRequest get(String id) {
		Optional<CreditCardAuthorizationRequest> o = creditCardAuthReqRepository.findById(id);
		if (o.isPresent()) {
			return o.get();
		} else {
			return null;
		}
	}
	
	public void save(CreditCardAuthorizationRequest value) {
		creditCardAuthReqRepository.save(value);
	}
	
	public List<CreditCardAuthorizationRequest> findByCustomerOrderId(String value) {
		return creditCardAuthReqRepository.findByCustomerOrderId(value);
	}

}
