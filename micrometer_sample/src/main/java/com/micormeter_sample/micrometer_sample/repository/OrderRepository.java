package com.micormeter_sample.micrometer_sample.repository;

import com.micormeter_sample.micrometer_sample.entity.Order;
import com.micormeter_sample.micrometer_sample.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerName(String customerName);
}
