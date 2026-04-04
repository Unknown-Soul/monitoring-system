package com.springbatch.batchsample.config;

import com.springbatch.batchsample.model.Product;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/*
 * ============================================================
 * PROCESSOR: CustomItemProcessor
 * ============================================================
 * - Transforms each item read from the source.
 * - Called for every item after Reader, before Writer.
 * - Can return null to FILTER OUT the item (won't be written).
 * - Can throw Exception to mark item as SKIPPED or FAILED.
 * 
 * Interview point: Used for business logic, validation, enrichment.
 */
@Component
class CustomItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product item) throws Exception {
        // Extract discount value from string (e.g., "10" -> 10%)
        int discount = Integer.parseInt(item.getDiscount().trim());
        
        // Extract price value from string (e.g., "100.0" -> 100.0)
        double price = Double.parseDouble(item.getPrice());
        
        // Calculate discounted price: price - (discount% of price)
        double discountAmount = (discount / 100.0) * price;
        double finalPrice = price - discountAmount;
        
        // Set the calculated final price
        item.setFinalPrice(finalPrice);
        
        // Return the processed item to be written
        return item;
    }
}

/*
 * Interview Q: What is the difference between ItemProcessor and Tasklet?
 * 
 * ItemProcessor: Processes items one by one (read → process → write in chunks).
 *                Best for ETL operations where you transform each record.
 *                
 * Tasklet: Simple task that executes once (or repeatedly).
 *          Best for simple operations like cleanup, one-time scripts.
 * 
 * Interview Q: Can processor return null?
 * Yes! Returning null filters the item - it won't be passed to the writer.
 * Use this to skip unwanted records.
 */