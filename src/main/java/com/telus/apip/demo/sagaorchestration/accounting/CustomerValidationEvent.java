package com.telus.apip.demo.sagaorchestration.accounting;

public class CustomerValidationEvent {
	
	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected String validationStatus;
	protected String customerOrderId;
	protected Customer customer;
	
	public CustomerValidationEvent() {
		
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getCausationId() {
		return causationId;
	}

	public void setCausationId(String causationId) {
		this.causationId = causationId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(String value) {
		this.validationStatus = value;
	}
	
	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}

	@Override
	public String toString() {
		return "CustomerValidationEvent [messageId=" + messageId + ", correlationId=" + correlationId + ", causationId="
				+ causationId + ", eventType=" + eventType + ", message=" + validationStatus + ", customerOrderId="
				+ customerOrderId + ", customer=[" + customer + "]]";
	}

	
}
