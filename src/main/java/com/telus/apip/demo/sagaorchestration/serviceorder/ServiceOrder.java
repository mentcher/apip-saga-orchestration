package com.telus.apip.demo.sagaorchestration.serviceorder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ServiceOrder {
	
	@Id
	private String serviceOrderId;
	private String customerOrderId;
	private String orderState;
	@ElementCollection
	private List<ServiceItem> serviceItemList;

	public ServiceOrder() {
		
	}
	
	public ServiceOrder(String serviceOrderId, String customerOrderId, List<ServiceItem> serviceItemList) {
		super();
		this.serviceOrderId = serviceOrderId;
		this.customerOrderId = customerOrderId;
		this.serviceItemList = serviceItemList;
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
	public List<ServiceItem> getServiceItemList() {
		return serviceItemList;
	}
	public void setServiceItemList(List<ServiceItem> serviceItemList) {
		this.serviceItemList = serviceItemList;
	}
	public void addServiceItem(ServiceItem value) {
		if (serviceItemList == null) {
			serviceItemList = new ArrayList<ServiceItem>();
		}
		serviceItemList.add(value);
	}
	@Override
	public String toString() {
		return "ServiceOrder [serviceOrderId=" + serviceOrderId + ", customerOrderId=" + customerOrderId
				+ ", serviceItemList=" + serviceItemList + "]";
	}

}
