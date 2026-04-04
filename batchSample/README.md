# Spring Batch Application - Quick Reference Guide

## Overview

This is a basic Spring Batch application that reads product data from a CSV file, processes it (calculates discounted price), and writes it to a MySQL database.

---

## Architecture Block Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    SPRING BATCH ARCHITECTURE                            │
└─────────────────────────────────────────────────────────────────────────────────────────┘

                                     ┌──────────────────────────┐
                                     │    APPLICATION LAUNCHER  │
                                     │   (JobLauncherApplication) │
                                     └────────────┬─────────────┘
                                                    │
                                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                        JOB REPOSITORY                                    │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │  Stores metadata: JobExecutions, StepExecutions, JobParameters, Status          │  │
│  │  Tables: BATCH_JOB_INSTANCE, BATCH_JOB_EXECUTION, BATCH_STEP_EXECUTION         │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
                                                    │
                                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                            JOB                                          │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │  JobExecutionListener (beforeJob / afterJob)                                    │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                         │                                              │
│                         ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐   │
│  │                              STEP                                                │   │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐ │   │
│  │  │                      STEP REPOSITORY                                      │ │   │
│  │  │   Stores: StepExecution, ReadCount, WriteCount, CommitCount, SkipCount   │ │   │
│  │  └────────────────────────────────────────────────────────────────────────────┘ │   │
│  │                                                                                 │   │
│  │  ┌───────────────────────────────────────────────────────────────────────────┐  │   │
│  │  │                    TRANSACTION MANAGER                                  │  │   │
│  │  │          Manages transactions for each chunk (batch of items)          │  │   │
│  │  └───────────────────────────────────────────────────────────────────────────┘  │   │
│  │                                                                                 │   │
│  │        ▼                    ▼                         ▼                       │   │
│  │  ┌─────────┐        ┌─────────────┐         ┌────────────┐                  │   │
│  │  │  READER │  ──▶   │  PROCESSOR  │  ──▶    │   WRITER   │                  │   │
│  │  └─────────┘        └─────────────┘         └────────────┘                  │   │
│  │       │                    │                       │                          │   │
│  │       ▼                    ▼                       ▼                          │   │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐   │   │
│  │  │                      CHUNK (N items per transaction)                    │   │   │
│  │  │   Example: chunk(5) = read 5 → process 5 → write 5 → commit            │   │   │
│  │  └─────────────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Flow Diagram

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│     CSV     │     │   READER    │     │  PROCESSOR  │
│   (Input)   │────▶│FlatFileItem │────▶│   (Logic)    │
│   data.csv  │     │   Reader    │     │ CustomItem   │
└─────────────┘     └─────────────┘     │   Processor  │
                                          └─────────────┘
                                                  │
                                                  ▼
                                    ┌─────────────────────────┐
                                    │    PROCESSED PRODUCT    │
                                    │  - productId             │
                                    │  - title                │
                                    │  - description          │
                                    │  - discount             │
                                    │  - finalPrice (NEW!)    │
                                    └─────────────────────────┘
                                                  │
                                                  ▼
                                    ┌─────────────────────────┐
                                    │      WRITER             │
                                    │  JdbcBatchItemWriter    │
                                    │  (beanMapped)          │
                                    └───────────┬─────────────┘
                                                │
                                                ▼
                                    ┌─────────────────────────┐
                                    │   MySQL DATABASE       │
                                    │  Table: products       │
                                    └─────────────────────────┘
```

---

## Core Components

### 1. Job Repository
```
Purpose: Stores all batch metadata
Tables Created:
├── BATCH_JOB_INSTANCE    (Job ID, version, status)
├── BATCH_JOB_EXECUTION  (Job start/end time, exit code)
├── BATCH_JOB_PARAMS     (Parameters passed to job)
├── BATCH_STEP_EXECUTION (Step details: read/write/skip counts)
└── BATCH_STEP_CONTEXT   (Step execution data)
```

### 2. Transaction Manager
```
Purpose: Manages database transactions for each chunk
- Ensures data consistency
- Commits after each chunk is processed
- Rolls back on failure
```

### 3. Job
```
Purpose: Top-level container for one batch operation
Contains: One or more Steps
Metadata: JobInstance, JobParameters, JobExecution
```

### 4. Step
```
Purpose: Smallest unit of work
Contains:
├── ItemReader    (Reads one item)
├── ItemProcessor (Transforms one item - optional)
├── ItemWriter    (Writes one item)
└── Chunk Size    (Items per transaction)
```

### 5. ItemReader
```
Implementation: FlatFileItemReader
├── Reads from CSV file
├── Maps columns to domain object
├── Skips header rows
└── Returns null when no more data
```

### 6. ItemProcessor
```
Implementation: CustomItemProcessor
├── Transforms input to output
├── Can return null to filter out items
├── Can throw exception to skip item
└── Optional - can be omitted
```

### 7. ItemWriter
```
Implementation: JdbcBatchItemWriter
├── Writes to database
├── Uses batch insert (performance)
├── beanMapped() maps bean properties to SQL parameters
└── :paramName → getParamName()
```

---

## Configuration Explained

### application.properties
```properties
# DataSource (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/boot_work
spring.datasource.username=root
spring.datasource.password=root

# Auto-create Spring Batch tables
spring.batch.jdbc.initialize-schema=always
```

### BatchConfig.java - Key Settings
```java
// Job: Top-level batch job
new JobBuilder("job", jobRepository)
    .listener(listener)    // Before/After callbacks
    .start(steps)          // First step
    .build();

// Step: Work unit with chunk processing
new StepBuilder("steps", jobRepository)
    .<Product, Product>chunk(5)  // Process 5 items per transaction
    .reader(reader)
    .processor(processor)
    .writer(writer)
    .build();

// Reader: CSV file with header skip
new FlatFileItemReaderBuilder<Product>()
    .linesToSkip(1)                    // Skip header
    .delimited()                       // Comma-separated
    .names("productId", "title", ...)  // Column mapping
    .targetType(Product.class)         // Map to POJO
    .build();

// Writer: JDBC batch insert
new JdbcBatchItemWriterBuilder<Product>()
    .sql("INSERT INTO products(...) VALUES (:productId, :title, ...)")
    .beanMapped()  // Auto-map bean properties to :param
    .build();
```

---

## Processing Flow (Step-by-Step)

```
1. APPLICATION STARTS
   │
   ▼
2. JOB LAUNCHER reads JobParameters
   │
   ▼
3. JOB REPOSITORY creates JobExecution
   │
   ▼
4. JOB STARTED (JobExecutionListener.beforeJob)
   │
   ▼
5. STEP EXECUTION begins
   │
   ▼
6. FOR EACH CHUNK (size = 5):
   │
   ├── 6.1 READER reads 5 items from CSV
   │       └── Product item1, item2, item3, item4, item5
   │
   ├── 6.2 PROCESSOR transforms each item
   │       └── Calculates finalPrice = price - (discount% * price)
   │
   ├── 6.3 WRITER writes 5 items to DB
   │       └── INSERT INTO products VALUES (...)
   │
   └── 6.4 TRANSACTION commits
   │
   ▼
7. Repeat until no more data
   │
   ▼
8. STEP COMPLETED
   │
   ▼
9. JOB COMPLETED (JobExecutionListener.afterJob)
   │
   ▼
10. JOB REPOSITORY updates final status
```

---

## Important Interview Points

### 1. Chunk-Oriented Processing
- Reads, processes, and writes in chunks (e.g., 5 items at a time)
- Each chunk = one transaction
- On failure, only the current chunk is rolled back

### 2. Reader → Processor → Writer Flow
```
Reader: Reads ONE item from source
Processor: Transforms ONE item (optional, can return null to filter)
Writer: Writes ONE item to destination
Repeat for chunk size, then commit
```

### 3. Job Repository vs Step Repository
- **JobRepository**: Stores Job-level metadata
- **StepRepository**: Stores Step-level execution details

### 4. Listeners Available
```
JobExecutionListener     - beforeJob(), afterJob()
StepExecutionListener    - beforeStep(), afterStep()
ChunkListener            - beforeChunk(), afterChunk()
ItemReadListener         - afterRead(), beforeRead(), onReadError()
ItemProcessListener      - afterProcess(), beforeProcess(), onProcessError()
ItemWriteListener        - afterWrite(), beforeWrite(), onWriteError()
```

### 5. Restartability
- Spring Batch is stateless - can restart failed jobs
- JobRepository tracks execution, restart from last successful chunk

### 6. Skip Policy
- `skipLimit(n)` - Skip up to n bad records
- `faultTolerant()` - Enable skip functionality

### 7. Different Readers/Writers
```
Readers:
├── FlatFileItemReader     (CSV, fixed-width files)
├── JdbcCursorItemReader   (Database cursor)
├── JdbcPagingItemReader   (Database with pagination)
├── QueueItemReader        (Message queues)
└── JsonItemReader         (JSON files)

Writers:
├── JdbcBatchItemWriter    (Database batch insert)
├── FlatFileItemWriter     (Write to files)
├── MultiResourceItemWriter (Multiple output files)
└── JsonFileItemWriter     (JSON output)
```

---

## Project Structure

```
batchSample/
├── pom.xml                                    # Maven dependencies
├── src/main/java/com/springbatch/batchsample/
│   ├── BatchSampleApplication.java           # Main class
│   ├── config/
│   │   ├── BatchConfig.java                  # Job & Step configuration
│   │   ├── CustomItemProcessor.java          # Business logic
│   │   └── JobCompletionNotificationImpl.java # Job listener
│   └── model/
│       └── Product.java                      # Domain object
├── src/main/resources/
│   ├── application.properties                # Configuration
│   ├── schema.sql                            # Table creation
│   └── data.csv                              # Input data
└── README.md                                 # This file
```

---

## Quick Commands

```bash
# Run the application
mvn spring-boot:run

# Or run as Java application
java -jar batchSample-0.0.1-SNAPSHOT.jar
```

---

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| No value supplied for parameter | SQL param name doesn't match bean property | Ensure `:paramName` matches `getParamName()` |
| No valid constructor | Missing no-arg constructor | Add `public Product() {}` |
| Header row parsed as data | linesToSkip not set | Add `.linesToSkip(1)` to reader |
| Deprecated SimpleStepBuilder | Spring Batch 6.0+ changes | Remove transactionManager from chunk() |