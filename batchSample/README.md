# Spring Batch Monitoring Project

## Overview
This project demonstrates a complete Spring Batch application with integrated monitoring using Micrometer, Prometheus, and Grafana. 
It reads product data from a CSV file, processes it (calculates discounted price), and writes the results to a MySQL database.

## What This Project Demonstrates

### Core Spring Batch Competency
- Job/Step/Reader/Processor/Writer architecture
- Chunk-oriented processing (5 items per transaction)
- Proper use of JobRepository for metadata storage
- ItemProcessor for business logic transformation
- JdbcBatchItemWriter for efficient database writes
- Job/Step listeners for lifecycle events

### Production-Ready Observability
- Micrometer for custom metrics collection
- Prometheus endpoint exposure via Spring Boot Actuator
- Grafana integration for visualization and alerting
- Docker-compose for reproducible monitoring stack
- Business metrics (products processed) + system metrics (job duration)

## Project Structure
```
batchSample/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/springbatch/batchsample/
│   │   │       ├── BatchSampleApplication.java   # Main application class
│   │   │       ├── config/
│   │   │       │   ├── BatchConfig.java          # Batch job configuration + metrics
│   │   │       │   ├── CustomItemProcessor.java  # Business logic + metric tracking
│   │   │       │   └── JobCompletionNotificationImpl.java  # Job listener
│   │   │       └── model/
│   │   │           └── Product.java              # Domain object
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       ├── schema.sql                        # Database table creation
│   │       └── data.csv                          # Input data
│   └── test/                                     # Test classes
├── prometheus/                                   # Prometheus configuration
│   └── prometheus.yml
├── grafana/                                      # Grafana provisioning
│   └── provisioning/
│       └── datasources/
│           └── datasource.yml
├── docker-compose.yml                            # Docker compose for monitoring stack
├── pom.xml                                       # Maven dependencies
└README.md                                        # This file
```

## Key Components Explained

### 1. Batch Configuration (`BatchConfig.java`)
- **Job Configuration**: Defines the Spring Batch job with listeners
- **Step Configuration**: Chunk-oriented step (5 items per transaction)
- **Reader**: `FlatFileItemReader` reads CSV, skips header, maps to `Product`
- **Processor**: `CustomItemProcessor` calculates discounted price
- **Writer**: `JdbcBatchItemWriter` inserts data using batch operations
- **Metrics Integration**: 
  - Counters: Products read/written/skipped, discounts calculated
  - Timers: Job duration, step duration
  - Gauge: Chunk size

### 2. Custom Processor (`CustomItemProcessor.java`)
- Implements `ItemProcessor<Product, Product>`
- Calculates final price: `price - (discount% * price)`
- Tracks metrics via Micrometer counters
- Can filter items by returning `null` (not used in this implementation)

### 3. Model (`Product.java`)
- POJO representing a product record
- Includes getters/setters for all fields
- **Critical**: No-arg constructor required for Spring Batch instantiation
- Fields: productId, title, description, price, discount, finalPrice

### 4. Job Listener (`JobCompletionNotificationImpl.java`)
- Implements `JobExecutionListener`
- Logs job start/completion events
- Demonstrates Spring Batch listener pattern

### 5. Configuration Files
- `application.properties`: 
  - Database connection (MySQL)
  - Actuator endpoints exposure (prometheus, health, metrics)
  - Spring Batch table initialization
- `pom.xml`: 
  - Spring Boot Starter Batch
  - Actuator + Micrometer Prometheus registry
  - MySQL and H2 dependencies
  - Web and test starters
- `schema.sql`: Creates `products` table in MySQL

### 6. Monitoring Stack
- **Prometheus**: Scrapes metrics from `/actuator/prometheus` endpoint
- **Grafana**: Visualizes metrics with pre-configured datasource
- **Docker Compose**: Orchestrates both services

## How to Run

### Option 1: Quick Start (Local)
```bash
# 1. Start the batch application
mvn spring-boot:run

# 2. Verify it's working:
# - Application logs show job start/completion
# - Metrics available at: http://localhost:8080/actuator/prometheus
# - Data inserted into MySQL database (boot_work.products table)

# 3. To see metrics in Prometheus/Grafana:
# Start monitoring stack in another terminal:
docker-compose up -d
# Then access:
#   Prometheus: http://localhost:9090
#   Grafana: http://localhost:3000 (admin/admin)
```

### Option 2: Full Stack with Docker
```bash
# 1. Start monitoring stack first
docker-compose up -d prometheus grafana

# 2. Start batch application (in another terminal)
mvn spring-boot:run

# 3. Access:
#   Application: http://localhost:8080
#   Prometheus: http://localhost:9090
#   Grafana: http://localhost:3000
```

## Quick Revision Notes (For Interview)

### Spring Batch Core Concepts
- **Job**: Top-level container for batch process (one or more Steps)
- **Step**: Independent unit of work (contains Reader → Processor → Writer)
- **Chunk**: Number of items processed in one transaction (e.g., chunk=5)
- **Reader**: Reads ONE item from source (returns null when done)
- **Processor**: Transforms ONE item (can return null to filter)
- **Writer**: Writes ONE item to destination
- **JobRepository**: Stores metadata (job/step executions, status)

### Processing Flow
```
[Application Start]
    ↓
[JobLauncher] 
    ↓
[JobRepository creates JobExecution]
    ↓
[Job STARTED (beforeJob listener)]
    ↓
[Step Execution begins]
    ↓
[FOR EACH CHUNK (size=5):]
    ├── Reader: reads 5 items from CSV
    ├── Processor: transforms each item (calculates discount)
    ├── Writer: writes 5 items to DB (batch insert)
    └── Transaction commits
    ↓
[Repeat until no more data]
    ↓
[Step COMPLETED]
    ↓
[Job COMPLETED (afterJob listener)]
    ↓
[JobRepository updates final status]
```

### Micrometer Metrics Explained
| Metric Type | Example | Purpose |
|-------------|---------|---------|
| **Counter** | `batch.products.read_total` | Monotonically increasing count |
| **Timer** | `batch.job.duration_seconds` | Measures duration (count, sum, max, etc.) |
| **Gauge** | `batch.chunk.size` | Current value (e.g., fixed chunk size) |

**Available at**: `http://localhost:8080/actuator/prometheus`
**PromQL Examples**:
- `rate(batch_products_written_total[5m])` → writes per second
- `batch_job_duration_seconds_count` → number of job executions
- `avg_over_time(batch_step_duration_seconds[1h])` → average step duration

### Key Implementation Details
1. **CSV Handling**: 
   - `linesToSkip(1)` ignores header row
   - Column mapping via `.names("productId","title",...)`
2. **Database Operations**:
   - `beanMapped()` auto-maps bean properties to SQL `:param`
   - Insert uses named parameters matching getters (e.g., `:finalPrice` → `getFinalPrice()`)
3. **Error Handling**:
   - Spring Batch provides built-in retry/skip mechanisms
   - Custom exceptions in processor can trigger skip logic
4. **Monitoring Best Practices**:
   - Business metrics (products processed) + system metrics (job duration)
   - Consistent naming: `batch.<entity>.<action>[_total|_seconds]`
   - Descriptions help with dashboard creation

### Common Interview Questions & Answers

**Q: What is the difference between ItemProcessor and Tasklet?**
- **ItemProcessor**: Processes items individually (read → process → write in chunks). Ideal for ETL.
- **Tasklet**: Simple interface with one `execute()` method. Good for one-time tasks (cleanup, setup).

**Q: Can ItemProcessor return null?**
- **Yes!** Returning null filters the item - it won't be passed to the Writer.
- Use case: Skip invalid records, apply business rules.

**Q: Why is a no-arg constructor required in the Product class?**
- Spring Batch uses reflection to instantiate objects via `BeanWrapperFieldSetMapper`.
- It calls the no-arg constructor first, then uses setters to populate fields.

**Q: How does chunk-oriented processing work?**
- Reads N items (chunk size) → Processes N items → Writes N items → Commits transaction
- Provides balance between performance (batch DB writes) and fault tolerance (limited rollback scope)

**Q: What tables does Spring Batch create?**
- `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, `BATCH_JOB_EXECUTION_PARAMS`
- `BATCH_STEP_EXECUTION`, `BATCH_STEP_EXECUTION_CONTEXT`
- Stores: job/step execution status, start/end times, read/write/skip counts

**Q: How do you monitor a Spring Batch job?**
- Built-in metrics via Micrometer (job/step execution counts, durations)
- Custom business metrics (using Counter/Timer/Gauge)
- External tools: Prometheus + Grafana for visualization and alerting
- JobRepository for execution history and restart capability

### Troubleshooting Tips
| Symptom | Likely Cause | Solution |
|---------|--------------|----------|
| No metrics in `/actuator/prometheus` | Actuator not exposed | Check `management.endpoints.web.exposure.include` |
| CSV header parsed as data | Missing `linesToSkip(1)` | Add `.linesToSkip(1)` to reader builder |
| SQL parameter missing | Bean property mismatch | Ensure `:paramName` matches `getParamName()` |
| Job fails to start | Missing dependencies | Verify `spring-boot-starter-batch` in pom |
| Database connection fails | Wrong URL/credentials | Check `spring.datasource.*` in properties |

## Files to Remember for Interview

| File | Key Points to Discuss |
|------|----------------------|
| `BatchConfig.java` | Job/Step configuration, metrics integration, reader/processor/writer setup |
| `CustomItemProcessor.java` | Business logic implementation, metric counters, null return for filtering |
| `Product.java` | Domain model, importance of no-arg constructor and getters/setters |
| `application.properties` | Database config, actuator endpoints, batch table initialization |
| `pom.xml` | Batch + Actuator + Micrometer dependencies |
| `docker-compose.yml` | How to orchestrate monitoring stack (Prometheus + Grafana) |
| `prometheus/prometheus.yml` | scrape_configs for targeting Spring Boot Actuator endpoint |

---
**Final Note**: This project demonstrates end-to-end batch processing with production-ready monitoring - ideal for showcasing both core Spring Batch competency and observability skills in interviews!