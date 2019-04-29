package com.telus.apip.demo.sagaorchestration.customerorder;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, String> {

}
