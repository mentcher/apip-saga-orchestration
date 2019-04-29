package com.telus.apip.demo.sagaorchestration.accounting;

public class EquipmentOrderEvent {

	protected String messageId;
	protected String correlationId;
	protected String causationId;
	protected String eventType;
	protected EquipmentOrder equipmentOrder;

	public EquipmentOrderEvent() {
		
	}
	
	public EquipmentOrderEvent(String messageId, String correlationId, String causationId, String eventType,
			EquipmentOrder equipmentOrder) {
		super();
		this.messageId = messageId;
		this.correlationId = correlationId;
		this.causationId = causationId;
		this.eventType = eventType;
		this.equipmentOrder = equipmentOrder;
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
	public EquipmentOrder getEquipmentOrder() {
		return equipmentOrder;
	}
	public void setEquipmentOrder(EquipmentOrder equipmentOrder) {
		this.equipmentOrder = equipmentOrder;
	}

	@Override
	public String toString() {
		return "CustomerOrderEvent [messageId=" + messageId + ", correlationId=" + correlationId + ", causationId="
				+ causationId + ", eventType=" + eventType + ", customerOrder=[" + equipmentOrder + "]]";
	}

}
