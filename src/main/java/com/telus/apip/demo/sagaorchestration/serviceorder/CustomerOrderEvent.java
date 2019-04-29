package com.telus.apip.demo.sagaorchestration.serviceorder;

public class CustomerOrderEvent {

	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected CustomerOrder customerOrder;

	public CustomerOrderEvent() {
		
	}
	
	public CustomerOrderEvent(String messageId, String correlationId, String causationId, String eventType,
			CustomerOrder customerOrder) {
		super();
		this.messageId = messageId;
		this.correlationId = correlationId;
		this.causationId = causationId;
		this.eventType = eventType;
		this.customerOrder = customerOrder;
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
	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}
	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public String toString() {
		return "CustomerOrderEvent [messageId=" + messageId + ", correlationId=" + correlationId + ", causationId="
				+ causationId + ", eventType=" + eventType + ", customerOrder=[" + customerOrder.toString() + "]]";
	}

}
