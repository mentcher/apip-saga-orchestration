package com.telus.apip.demo.sagaorchestration.accounting;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CreditCardAuthorizationRequest {
	
	public enum CredtCardAuthEvent {
			CustomerValidated(1),      //0001
			EquipmentOrderCreated(2),  //0010
			ServiceOrderCreated(4);    //0100
		
		private int value;
		public int getValue() {
			return this.value;
		}
		
		CredtCardAuthEvent(int value) {
			this.value = value;
		}
	}
	
	@Id
	private String creditCardAuthorizationRequestId;
	private String customerId;
	private String customerOrderId;
	//Need to maintain state to make sure after the customer order has been created
	//The customer validation has passed, the equipment order created, 
	//the service order created.  Then send the message back to the customer order
	//that the payment has been approved
	private int status;
	private String amount;
	private String transactionReferenceNumber;
	
	
	
	public CreditCardAuthorizationRequest(String creditCardAuthorizationRequestId, String customerId,
			String customerOrderId, int status, String amount, String transactionReferenceNumber) {
		super();
		this.creditCardAuthorizationRequestId = creditCardAuthorizationRequestId;
		this.customerId = customerId;
		this.customerOrderId = customerOrderId;
		this.status = status;
		this.amount = amount;
		this.transactionReferenceNumber = transactionReferenceNumber;
	}

	/*
	 * Update the status of the authorization request based on the event
	 * - CustomerValidated
	 * - EquipmentOrderCreated
	 * - ServiceOrderCreated
	 */
	public void updateStatusByEvent(CredtCardAuthEvent event) {
		this.status = this.status & event.value;
	}
	
	public boolean allEventsPassed() {
		return this.status == 7;
	}

	public String getCreditCardAuthorizationRequestId() {
		return creditCardAuthorizationRequestId;
	}

	public void setCreditCardAuthorizationRequestId(String creditCardAuthorizationRequestId) {
		this.creditCardAuthorizationRequestId = creditCardAuthorizationRequestId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTransactionReferenceNumber() {
		return transactionReferenceNumber;
	}

	public void setTransactionReferenceNumber(String transactionReferenceNumber) {
		this.transactionReferenceNumber = transactionReferenceNumber;
	}
	
}
