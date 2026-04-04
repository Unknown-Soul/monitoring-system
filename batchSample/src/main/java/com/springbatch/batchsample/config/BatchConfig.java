package com.springbatch.batchsample.config;

import com.springbatch.batchsample.model.Product;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    // ============================================================
    // JOB CONFIGURATION
    // A Job is the main entity in Spring Batch - it's a container for Steps.
    // ============================================================
    
    @Bean
    public Job jobBean(JobRepository jobRepository,
                       JobCompletionNotificationImpl listener,
                       Step steps
    ) {
        // JobBuilder creates a Job with a unique name and JobRepository
        // JobRepository stores job metadata (status, execution info) in DB
        return new JobBuilder("job", jobRepository)
                .listener(listener)      // Executes callback methods before/after job
                .start(steps)            // Step to execute
                .build();
    }

    // ============================================================
    // STEP CONFIGURATION
    // A Step is the smallest unit of work in Spring Batch.
    // It contains Reader, Processor, and Writer (the ETL pipeline).
    // ============================================================
    
    @Bean
    public Step steps(JobRepository jobRepository,
                      ItemReader<Product> reader,
                      ItemProcessor<Product, Product> processor,
                      ItemWriter<Product> writer,
                      DataSourceTransactionManager transactionManager
    ) {
        // StepBuilder creates a Step with name and JobRepository
        // chunk(5) = process 5 records at a time (batch size)
        // Each chunk is processed in a single transaction
        return new StepBuilder("steps", jobRepository)
                .<Product, Product>chunk(5)  // <InputType, OutputType>
                .reader(reader)      // READ: Fetch data from source (CSV file)
                .processor(processor) // PROCESS: Transform data (apply business logic)
                .writer(writer)      // WRITE: Save processed data to destination (DB)
                .build();
    }

    // ============================================================
    // READER: FlatFileItemReader
    // Reads data from CSV file and maps to domain object.
    // ============================================================
    
    @Bean
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("itemReader")                    // Unique name for the reader
                .resource(new ClassPathResource("data.csv")) // Input file location
                .linesToSkip(1)                        // Skip header row (line 1)
                .delimited()                           // File uses delimiter (comma by default)
                .names("productId", "title", "description", "price", "discount") // Column names
                .targetType(Product.class)            // Map columns to Product fields
                .build();
    }

    // ============================================================
    // PROCESSOR: CustomItemProcessor
    // Applies business logic to each item.
    // Return null to skip the item (filter out).
    // ============================================================
    
    @Bean
    public ItemProcessor<Product, Product> processor(CustomItemProcessor customItemProcessor) {
        return customItemProcessor;
    }

    // ============================================================
    // WRITER: JdbcBatchItemWriter
    // Writes processed items to database using JDBC batch operations.
    // beanMapped() maps bean properties to SQL named parameters.
    // ============================================================
    
    @Bean
    public ItemWriter<Product> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .sql("INSERT INTO products(product_id, title, description, discount, discounted_price) " +
                     "VALUES (:productId, :title, :description, :discount, :finalPrice)")
                .dataSource(dataSource)
                .beanMapped()  // Maps :productId -> getProductId(), :finalPrice -> getFinalPrice()
                .build();
    }
}

/*
 * ========================================================================
 * INTERVIEW CHEAT SHEET - SPRING BATCH KEY CONCEPTS:
 * ========================================================================
 * 
 * 1. JOB: A batch process consisting of one or more Steps.
 * 
 * 2. STEP: Smallest unit of work. Contains:
 *    - Reader: Reads from source (file, DB, queue)
 *    - Processor: Transforms data (optional)
 *    - Writer: Writes to destination (file, DB)
 * 
 * 3. CHUNK: Number of items processed in one transaction.
 *    - If chunk=5, reads 5 items → processes 5 items → writes 5 items → commits
 * 
 * 4. JOB REPOSITORY: Stores metadata (job executions, step executions, status)
 * 
 * 5. READER → PROCESSOR → WRITER flow:
 *    - Reader reads ONE item
 *    - Processor transforms ONE item
 *    - Writer writes ONE item
 *    - Repeat for chunk size, then commit transaction
 * 
 * 6. itemProcessor can return null to FILTER (skip) items from writing.
 * 
 * 7. linesToSkip(1) - skips header row in CSV files.
 * 
 * 8. beanMapped() - maps bean properties to SQL :parameter names automatically.
 * ========================================================================
 */