package com.telus.apip.demo.sagaorchestration.serviceorder;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

@Embeddable
public class ServiceLineItem {
	
	private String serviceCode;
	private BigDecimal recurringCharge;
	
	public ServiceLineItem() {
		
	}

	public ServiceLineItem(String serviceCode, BigDecimal recurringCharge) {
		super();
		this.serviceCode = serviceCode;
		this.recurringCharge = recurringCharge;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public BigDecimal getRecurringCharge() {
		return recurringCharge;
	}
	public void setRecurringCharge(BigDecimal recurringCharge) {
		this.recurringCharge = recurringCharge;
	}
	
}
