package com.telus.apip.demo.sagaorchestration.accounting;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ServiceOrder {
	
	@Id
	private String serviceOrderId;
	private String customerOrderId;
	private String orderState;

	public ServiceOrder() {
		
	}
	
	public ServiceOrder(String serviceOrderId, String customerOrderId) {
		super();
		this.serviceOrderId = serviceOrderId;
		this.customerOrderId = customerOrderId;
	}
	public String getServiceOrderId() {
		return serviceOrderId;
	}
	public void setServiceOrderId(String serviceOrderId) {
		this.serviceOrderId = serviceOrderId;
	}
	public String getCustomerOrderId() {
		return customerOrderId;
	}
	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	@Override
	public String toString() {
		return "ServiceOrder [serviceOrderId=" + serviceOrderId + ", customerOrderId=" + 
				customerOrderId + "]";
	}

}
