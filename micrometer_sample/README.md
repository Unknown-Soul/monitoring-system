# micrometer_sample - Spring Boot Micrometer Demo with JPA and Batch

## Overview
This is a more comprehensive Spring Boot application demonstrating Micrometer integration in a realistic application context. It appears to be an e-commerce-style application with JPA entities, repositories, services, and controllers, showing how to integrate Micrometer metrics in a multi-layered application.

## What This Project Shows
- Spring Boot application with multiple layers (controller, service, repository, entity)
- Spring Data JPA for database persistence
- Spring Batch integration (based on dependencies)
- Micrometer metrics integration throughout the application
- REST API for interacting with the system
- More complex business domain (orders, products, order items)

## Project Structure
```
micrometer_sample/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/micormeter_sample/micrometer_sample/
│   │   │       ├── MicrometerSampleApplication.java   # Main application class
│   │   │       ├── entity/                            # JPA entities
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   └── Product.java
│   │   │       ├── enums/                             # Enums
│   │   │       │   └── OrderStatus.java
│   │   │       ├── repository/                        # Spring Data JPA repositories
│   │   │       │   ├── OrderItemRepository.java
│   │   │       │   ├── OrderRepository.java
│   │   │       │   └── ProductRepository.java
│   │   │       ├── service/                           # Service interfaces
│   │   │       │   ├── OrderService.java
│   │   │       │   └── ProductService.java
│   │   │       ├── service/implimentation/            # Service implementations (note: typo in package name)
│   │   │       │   ├── OrderServiceImplimentation.java
│   │   │       │   └── ProductServiceImplimentation.java
│   │   │       └── Controller/                        # REST controllers (note: capitalized)
│   │   │           ├── OrderController.java
│   │   │           └── ProductController.java
│   │   └── resources/                                 # Application resources
│   │       ├── application.properties                 # Configuration
│   │       └── ...                                    # Other resources
│   └── test/                                          # Test classes
├── .mvn/                                              # Maven wrapper
├── HELP.md                                            # Getting started guide
├── mvnw                                               # Maven wrapper script
├── mvnw.cmd                                           # Maven wrapper for Windows
├── pom.xml                                            # Maven dependencies
└README.md                                             # This file
```

## Key Components Explained

### Entities (src/main/java/com/micormeter_sample/micrometer_sample/entity/)
- **Order.java** - Represents a customer order
- **OrderItem.java** - Represents an item within an order
- **Product.java** - Represents a product that can be ordered
- **OrderStatus.java** - Enum for order statuses (PENDING, SHIPPED, DELIVERED, etc.)

### Repositories (src/main/java/com/micormeter_sample/micrometer_sample/repository/)
- **OrderRepository.java** - Spring Data JPA repository for Order entities
- **OrderItemRepository.java** - Spring Data JPA repository for OrderItem entities
- **ProductRepository.java** - Spring Data JPA repository for Product entities
- These extend JpaRepository and provide CRUD operations out-of-the-box

### Services (src/main/java/com/micormeter_sample/micrometer_sample/service/)
- **OrderService.java** - Interface defining order-related business operations
- **ProductService.java** - Interface defining product-related business operations
- **OrderServiceImplimentation.java** - Implementation of OrderService (note: typo in package name)
- **ProductServiceImplimentation.java** - Implementation of ProductService (note: typo in package name)

### Controllers (src/main/java/com/micormeter_sample/micrometer_sample/Controller/)
- **OrderController.java** - REST controller for order-related endpoints
- **ProductController.java** - REST controller for product-related endpoints
- Note: Package name is capitalized "Controller" (unconventional but functional)

## Key Files to Examine

### MicrometerSampleApplication.java
The main application class that starts the Spring Boot application.

### application.properties
Configuration file that would contain:
- Database connection details (likely MySQL based on dependencies)
- Micrometer/Prometheus configuration
- Other application settings

### pom.xml Dependencies Analysis
The pom.xml shows this is a more sophisticated application than sample1:
- `spring-boot-starter-actuator` - For production-ready features
- `spring-boot-starter-batch` - Spring Batch integration (unlike sample1)
- `spring-boot-starter-webmvc` - Web functionality
- `spring-boot-starter-data-jpa` - JPA for database access
- `spring-boot-starter-validation` - Bean validation
- `micrometer-registry-prometheus` - Prometheus metrics export
- `lombok` - For reducing boilerplate code (getters/setters, constructors)
- MySQL connector - For database connectivity
- Test dependencies for batch, webmvc, and actuator

## How to Run
```bash
# 1. Start the application
./mvnw spring-boot:run
# or
mvn spring-boot:run

# 2. Access REST endpoints (examples):
# GET all products: http://localhost:8080/api/products
# GET all orders: http://localhost:8080/api/orders

# 3. View Prometheus metrics:
curl http://localhost:8080/actuator/prometheus
```

## Learning Points
This demo teaches:
1. How to structure a multi-layered Spring Boot application
2. How to use Spring Data JPA for database persistence
3. How to organize business logic in service layers
4. How to create REST APIs with Spring MVC
5. How to integrate Micrometer metrics in a real application
6. How Spring Batch can be combined with other Spring technologies
7. Proper use of repositories, services, and controllers

## Relationship to batchSample Project
This project shows a different approach to Micrometer integration compared to your batchSample:

**Similarities:**
- Both use Micrometer with Prometheus registry
- Both expose metrics via `/actuator/prometheus`
- Both demonstrate production-ready Spring Boot practices

**Differences:**
- **Architecture**: This is a layered web application (controller→service→repository), while batchSample is a batch processing application
- **Data Access**: This uses Spring Data JPA with MySQL, while batchSample uses JdbcBatchItemWriter for direct SQL
- **Business Logic**: This has service layers with business logic, while batchSample puts logic in ItemProcessor
- **Batch Processing**: batchSample is primarily a batch application (CSV → DB processing), while this appears to be a web application that happens to have batch dependencies
- **Monitoring Focus**: batchSample focuses on batch-specific metrics (job/step duration, chunk processing), while this would likely focus on web/microservice metrics (request counts, latency, error rates)

## Quick Commands
```bash
# Run the application
mvn spring-boot:run

# Test endpoints (adjust URLs based on actual controller mappings)
curl http://localhost:8080/api/products
curl http://localhost:8080/api/orders

# View metrics
curl http://localhost:8080/actuator/prometheus
```