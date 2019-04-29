package com.telus.apip.demo.sagaorchestration.equiporder;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EquipmentOrderRepository extends CrudRepository<EquipmentOrder, String> {
	
	List<EquipmentOrder> findByCustomerOrderId(@Param("customerOrderId") String customerOrderId);

}
