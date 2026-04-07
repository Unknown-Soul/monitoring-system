package com.micormeter_sample.micrometer_sample.service;

import com.micormeter_sample.micrometer_sample.co.OrderCO;
import com.micormeter_sample.micrometer_sample.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderCO orderCO);
    Order getOrderById(Long id);
    List<Order> getAllOrders();
    Order updateOrderStatus(Long id, String status);
}
