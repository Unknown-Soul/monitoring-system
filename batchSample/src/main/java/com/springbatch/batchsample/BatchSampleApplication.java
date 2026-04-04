package com.springbatch.batchsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchSampleApplication {

    public static void main(String[] args) {
        // exist the application once completed
        System.exit(SpringApplication.exit(SpringApplication.run(BatchSampleApplication.class, args)));
    }

}
