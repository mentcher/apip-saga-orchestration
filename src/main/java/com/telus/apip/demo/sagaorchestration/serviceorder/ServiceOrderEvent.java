package com.telus.apip.demo.sagaorchestration.serviceorder;

public class ServiceOrderEvent {
	
	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected ServiceOrder serviceOrder;
	
	public ServiceOrderEvent() {
		
	}
	
	public ServiceOrderEvent(String messageId, String correlationId, String causationId, String eventType,
			ServiceOrder serviceOrder) {
		super();
		this.messageId = messageId;
		this.correlationId = correlationId;
		this.causationId = causationId;
		this.eventType = eventType;
		this.serviceOrder = serviceOrder;
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

	public ServiceOrder getServiceOrder() {
		return serviceOrder;
	}

	public void setServiceOrder(ServiceOrder serviceOrder) {
		this.serviceOrder = serviceOrder;
	}

	@Override
	public String toString() {
		return "ServiceOrderEvent [messageId=" + messageId + ", correlationId=" + correlationId + ", causationId="
				+ causationId + ", eventType=" + eventType + ", serviceOrder=[" + serviceOrder + "]]";
	}
	
	

}
