package com.monitoring.sampleone.contorller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrometheusIntegrationController {

    final static Logger logger = LoggerFactory.getLogger(PrometheusIntegrationController.class);

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
