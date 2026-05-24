# sample1 - Spring Boot Actuator & Prometheus Demo

## Overview
This is a minimal Spring Boot application demonstrating basic integration with Prometheus metrics through Spring Boot Actuator. It serves as a simple example of how to expose application metrics for monitoring purposes.

## What This Project Shows
- Basic Spring Boot application structure
- Spring Boot Actuator integration
- Prometheus metrics endpoint exposure
- Simple REST controller that generates logs/metrics when accessed

## Project Structure
```
sample1/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/monitoring/sampleone/
│   │   │       ├── Sample1Application.java     # Main application class
│   │   │       └── contorller/
│   │   │           └── PrometheusIntegrationController.java  # Simple REST endpoint
│   │   └── resources/
│   │       ├── application.properties          # Configuration (exposes prometheus endpoint)
│   │       ├── data/                           # Data directory (empty)
│   │       ├── prometheus-config/              # Prometheus configuration samples
│   │       ├── static/                         # Static resources
│   │       └── templates/                      # Template files
│   └── test/                                   # Test classes
├── .mvn/                                       # Maven wrapper
├── HELP.md                                     # Getting started guide
├── mvnw                                        # Maven wrapper script
├── mvnw.cmd                                    # Maven wrapper for Windows
├── pom.xml                                     # Maven dependencies
└── README.md                                   # This file
```

## Key Files Explained

### application.properties
```properties
spring.application.name=sample1
management.endpoints.web.exposure.include=prometheus
```
- Exposes only the Prometheus endpoint via Actuator
- Metrics available at: `http://localhost:8080/actuator/prometheus`

### PrometheusIntegrationController.java
```java
@RestController
public class PrometheusIntegrationController {
    @GetMapping("/something")
    public ResponseEntity<String> createLogs() throws InterruptedException {
        logger.warn("createLogs()");
        Thread.sleep(34);
        logger.error("Error");
        Thread.sleep(341);
        logger.debug("Debug");
        return ResponseEntity.ok("createLogs()");
    }
}
```
- Simple endpoint that generates various log levels when accessed
- Useful for testing if logging/monitoring is working
- Creates artificial delays to simulate work

## How to Run
```bash
# 1. Start the application
./mvnw spring-boot:run
# or
mvn spring-boot:run

# 2. Access the endpoint to generate metrics:
curl http://localhost:8080/something

# 3. View Prometheus metrics:
curl http://localhost:8080/actuator/prometheus
```

## Dependencies (pom.xml)
- `spring-boot-starter-actuator` - Provides production-ready features including metrics
- `spring-boot-starter-webmvc` - For building web applications
- `micrometer-registry-prometheus` - Exposes metrics in Prometheus format
- Test dependencies for actuator and web

## Learning Points
This demo teaches:
1. How to enable Spring Boot Actuator
2. How to expose specific endpoints (in this case, only prometheus)
3. Basic REST controller creation
4. How metrics are automatically collected for Spring MVC endpoints
5. Basic project structure with Maven

## Relationship to batchSample Project
This is a much simpler demonstration than the `batchSample` project:
- **No batch processing** - just a simple web app
- **No business logic** - just logging when endpoint is hit
- **No database integration** - purely in-memory demo
- **Limited monitoring** - only exposes actuator prometheus endpoint
- **No custom metrics** - relies only on auto-configured metrics

The `batchSample` project builds upon these concepts by adding:
- Actual batch processing workflow (CSV → DB)
- Custom business metrics (products processed, discounts calculated)
- Job/Step lifecycle monitoring
- Docker-compose setup for full monitoring stack (Prometheus + Grafana)
- More sophisticated metric tracking using Micrometer Counter/Timer/Gauge

## Quick Commands
```bash
# Run the application
mvn spring-boot:run

# Test the endpoint
curl http://localhost:8080/something

# View metrics
curl http://localhost:8080/actuator/prometheus
```