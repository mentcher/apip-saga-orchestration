package com.telus.apip.demo.sagaorchestration.equiporder;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CourierRequest {
	
	@Id
	private String courierRequestId;
	//Need to track which order as part of orchestration
	//Can we use the correlation id from the event, but then we need to store events themselves
	private String customerOrderId;
	private String trackingNumber;
	private String deliveryAddress;
	public CourierRequest(String courierRequestId, String customerOrderId, String trackingNumber,
			String deliveryAddress) {
		super();
		this.courierRequestId = courierRequestId;
		this.customerOrderId = customerOrderId;
		this.trackingNumber = trackingNumber;
		this.deliveryAddress = deliveryAddress;
	}
	public String getCourierRequestId() {
		return courierRequestId;
	}
	public void setCourierRequestId(String courierRequestId) {
		this.courierRequestId = courierRequestId;
	}
	public String getCustomerOrderId() {
		return customerOrderId;
	}
	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	@Override
	public String toString() {
		return "CourierRequest [courierRequestId=" + courierRequestId + ", customerOrderId=" + customerOrderId
				+ ", trackingNumber=" + trackingNumber + ", deliveryAddress=" + deliveryAddress + "]";
	}

	
	
}
