package com.telus.apip.demo.sagaorchestration.equiporder;

public class CourierRequestEvent {
	
	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected CourierRequest courierRequest;
	
	public CourierRequestEvent() {
		
	}
	
	public CourierRequestEvent(String messageId, String correlationId, String causationId,
			CourierRequest courierRequest) {
		super();
		this.messageId = messageId;
		this.correlationId = correlationId;
		this.causationId = causationId;
		this.courierRequest = courierRequest;
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

	public CourierRequest getCourierRequest() {
		return courierRequest;
	}
	public void setCourierRequest(CourierRequest courierRequest) {
		this.courierRequest = courierRequest;
	}
	@Override
	public String toString() {
		return "CourierRequestEvent [messageId=" + messageId + ", correlationId=" + correlationId + ", causationId="
				+ causationId + ", courierRequest=[" + courierRequest + "]]";
	}
	

}
