package com.telus.apip.demo.sagaorchestration.accounting;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EquipmentOrder {

	@Id
	private String eqipmentOrderId;
	private String customerOrderId;
	private String customerId;
	private String installType;
	private String courierTrackingNumber;
	private String orderState;
	@ElementCollection
	private List<String> productCodes;

	public String getEqipmentOrderId() {
		return eqipmentOrderId;
	}

	public void setEqipmentOrderId(String eqipmentOrderId) {
		this.eqipmentOrderId = eqipmentOrderId;
	}

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
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

	public String getCourierTrackingNumber() {
		return courierTrackingNumber;
	}

	public void setCourierTrackingNumber(String courierTrackingNumber) {
		this.courierTrackingNumber = courierTrackingNumber;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public List<String> getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(List<String> productCodes) {
		this.productCodes = productCodes;
	}

	public void addProductCode(String value) {
		if (productCodes == null) {
			productCodes = new ArrayList<>();
		}
		productCodes.add(value);
	}

}
