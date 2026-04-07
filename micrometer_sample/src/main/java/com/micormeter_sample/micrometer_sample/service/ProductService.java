package com.micormeter_sample.micrometer_sample.service;

import com.micormeter_sample.micrometer_sample.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product createProduct(String name, BigDecimal price);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, String name, BigDecimal price);
    void deleteProduct(Long id);
}
