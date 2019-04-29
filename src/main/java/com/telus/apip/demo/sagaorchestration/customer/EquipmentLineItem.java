package com.telus.apip.demo.sagaorchestration.customer;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

@Embeddable
public class EquipmentLineItem {
	
	private String productCode;
	private BigDecimal amount;
	
	public EquipmentLineItem() {
		
	}
	
	public EquipmentLineItem(String productCode, BigDecimal amount) {
		super();
		this.productCode = productCode;
		this.amount = amount;
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	

}
