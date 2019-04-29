package com.telus.apip.demo.sagaorchestration.customer;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {
	
	public final static String STATUS_ACTIVE = "ACTIVE";
	public final static String STATUS_CANCELLED = "CANCELLED";
	public final static String STATUS_DELINQUENT = "DELINQUENT";
	

	@Id
	private String customerId;
	private String customerName;
	private String addressLine;
	private String phoneNumber;
	// ACTIVE, CANCELLED, DELINQUENT
	private String customerStatus;

	public Customer() {

	}

	public Customer(String customerId, String customerName, String addressLine, String phoneNumber,
			String customerStatus) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.addressLine = addressLine;
		this.phoneNumber = phoneNumber;
		this.customerStatus = customerStatus;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}

}
