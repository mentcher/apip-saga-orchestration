package com.telus.apip.demo.sagaorchestration.equiporder;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EquipmentOrderService {

	@Autowired
	EquipmentOrderRepository repository;

	public EquipmentOrder get(String equipmentOrderId) {
		Optional<EquipmentOrder> eoo = repository.findById(equipmentOrderId);
		if (eoo.isPresent()) {
			return eoo.get();
		} else {
			return null;
		}

	}

	public List<EquipmentOrder> findByCustomerOrderId(String customerOrderId) {
		return repository.findByCustomerOrderId(customerOrderId);
	}

	public void save(EquipmentOrder value) {
		repository.save(value);
	}

}
