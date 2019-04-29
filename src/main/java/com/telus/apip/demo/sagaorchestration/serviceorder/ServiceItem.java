package com.telus.apip.demo.sagaorchestration.serviceorder;

import javax.persistence.Embeddable;

@Embeddable
public class ServiceItem {
	
	private String serviceItemCode;

	public ServiceItem(String serviceItemCode) {
		super();
		this.serviceItemCode = serviceItemCode;
	}

	public String getServiceItemCode() {
		return serviceItemCode;
	}
	

}
