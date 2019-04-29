package com.telus.apip.demo.sagaorchestration.accounting;

public class CreditCardAuthorizationEvent {

	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected CreditCardAuthorizationRequest  creditCardAuthReq;

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
	public CreditCardAuthorizationRequest getCreditCardAuthReq() {
		return creditCardAuthReq;
	}
	public void setCreditCardAuthReq(CreditCardAuthorizationRequest creditCardAuthReq) {
		this.creditCardAuthReq = creditCardAuthReq;
	}
	
	
	
}
