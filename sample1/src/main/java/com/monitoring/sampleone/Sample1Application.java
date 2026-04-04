package com.monitoring.sampleone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Sample1Application {

    final static Logger logger =  LoggerFactory.getLogger(Sample1Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Sample1Application.class, args);
    }
//
//    @GetMapping("/something")
//    public ResponseEntity<String> createLogs(){
//        logger.warn("This is a test");
//        return ResponseEntity.ok("This is a test");
//    }
}
