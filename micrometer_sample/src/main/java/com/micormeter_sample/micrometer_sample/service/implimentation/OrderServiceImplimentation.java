package com.micormeter_sample.micrometer_sample.service.implimentation;

import com.micormeter_sample.micrometer_sample.co.OrderCO;
import com.micormeter_sample.micrometer_sample.entity.Order;
import com.micormeter_sample.micrometer_sample.entity.OrderItem;
import com.micormeter_sample.micrometer_sample.entity.Product;
import com.micormeter_sample.micrometer_sample.enums.OrderStatus;
import com.micormeter_sample.micrometer_sample.repository.OrderRepository;
import com.micormeter_sample.micrometer_sample.repository.ProductRepository;
import com.micormeter_sample.micrometer_sample.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImplimentation implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Order createOrder(OrderCO orderCO) {
        Order order = new Order();
        order.setCustomerName(orderCO.getCustomerName());
        order.setDiscountPercent(orderCO.getDiscount() != null ? orderCO.getDiscount() : BigDecimal.ZERO);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCO.OrderItemData itemData : orderCO.getItems()) {
            Optional<Product> productOpt = productRepository.findById(itemData.getProductId());
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found with id: " + itemData.getProductId());
            }

            Product product = productOpt.get();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemData.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemData.getQuantity())));
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            subtotal = subtotal.add(orderItem.getSubtotal());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(subtotal);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (orderCO.getDiscount() != null && orderCO.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subtotal.multiply(orderCO.getDiscount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        order.setDiscountAmount(discountAmount);

        BigDecimal finalTotal = subtotal.subtract(discountAmount);
        order.setTotalAmount(finalTotal);
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
