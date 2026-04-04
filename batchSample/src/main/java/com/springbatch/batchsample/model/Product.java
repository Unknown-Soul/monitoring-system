package com.springbatch.batchsample.model;

/*
 * ============================================================
 * MODEL: Product
 * ============================================================
 * - Domain object representing a single record in the batch.
 * - Used by Reader to store parsed CSV data.
 * - Used by Writer to map properties to SQL parameters.
 * 
 * IMPORTANT: Must have:
 * 1. No-arg constructor (required by BeanWrapperFieldSetMapper for instantiation)
 * 2. Getters and Setters for all fields used in mapping
 * 
 * Interview Q: Why no-arg constructor?
 * Spring Batch uses reflection to create the object, then populates
 * fields using setters. Without no-arg constructor, parsing fails.
 */
public class Product {

    private String productId;
    private String title;
    private String description;
    private String productPrice;
    private String price;
    private String discount;
    private double finalPrice;

    // Required for Spring Batch to instantiate the object
    public Product() {
    }

    // All-args constructor (optional, but useful)
    public Product(String productId, String title, String description, 
                   String productPrice, String price, String discount) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.productPrice = productPrice;
        this.price = price;
        this.discount = discount;
    }

    // Getters and Setters
    // Naming convention: getXxx() / setXxx() enables bean mapping

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}

/*
 * Interview Q: How does beanMapped() work in JdbcBatchItemWriter?
 * 
 * It uses Spring's BeanWrapper to map property names to SQL parameters.
 * :productId  → getProductId()
 * :title      → getTitle()
 * :finalPrice → getFinalPrice()
 * 
 * Property name must exactly match getter method name (minus "get" and lowercase first letter).
 */