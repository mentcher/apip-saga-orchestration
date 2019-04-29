package com.telus.apip.demo.sagaorchestration.equiporder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomerOrder {

	@Id
	private String customerOrderId;
	private String customerOrderStatus;
	private String customerId;
	private String installType; //SELF or TECH
	private String deliveryAddress;

	@ElementCollection
	private List<EquipmentLineItem> equipmentList;
	
	@ElementCollection
	private List<ServiceLineItem> serviceList;

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderid) {
		this.customerOrderId = customerOrderid;
	}

	public String getCustomerOrderStatus() {
		return customerOrderStatus;
	}

	public void setCustomerOrderStatus(String customerOrderStatus) {
		this.customerOrderStatus = customerOrderStatus;
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

	public List<EquipmentLineItem> getEquipmentList() {
		return equipmentList;
	}

	public void setEquipmentList(List<EquipmentLineItem> equipmentList) {
		this.equipmentList = equipmentList;
	}

	public List<ServiceLineItem> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ServiceLineItem> serviceList) {
		this.serviceList = serviceList;
	}

	
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public void addServiceItem(ServiceLineItem value) {
		if (serviceList == null) {
			serviceList = new ArrayList<ServiceLineItem>();
		}
		serviceList.add(value);
	}

	public void addEquipmentItem(EquipmentLineItem value) {
		if (equipmentList == null) {
			equipmentList = new ArrayList<EquipmentLineItem>();
		}
		equipmentList.add(value);
	}

	private String equipmentListToString() {
		StringBuffer buf = new StringBuffer();
		if (equipmentList != null) {
			for (EquipmentLineItem e : equipmentList) {
				buf.append("{" + e.getProductCode() + "," + e.getAmount() + "}");
			}
		}
		return buf.toString();
	}

	private String serviceListToString() {
		StringBuffer buf = new StringBuffer();
		if (serviceList != null) {
			for (ServiceLineItem s : serviceList) {
				buf.append("{" + s.getServiceCode() + "," + s.getRecurringCharge() + "}");
			}
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		return "CustomerOrder [customerOrderId=" + customerOrderId + ", customerOrderStatus=" + customerOrderStatus
				+ ", customerId=" + customerId + ", equipmentList=[" + equipmentListToString() + "], serviceList=["
				+ serviceListToString() + "]]";
	}

}
