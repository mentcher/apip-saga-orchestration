package com.telus.apip.demo.sagaorchestration.accounting;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomerOrder {

	@Id
	private String customerOrderId;
	private String customerOrderStatus;
	private String customerId;
	private String installType; //SELF or TECH
	private String deliveryAddress;

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderid) {
		this.customerOrderId = customerOrderid;
	}

	public String getCustomerOrderStatus() {
		return customerOrderStatus;
	}

	public void setCustomerOrderStatus(String customerOrderStatus) {
		this.customerOrderStatus = customerOrderStatus;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getInstallType() {
		return installType;
	}

	public void setInstallType(String installType) {
		this.installType = installType;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}


	@Override
	public String toString() {
		return "CustomerOrder [customerOrderId=" + customerOrderId + ", customerOrderStatus=" + customerOrderStatus
				+ ", customerId=" + customerId +"]";
	}

}
