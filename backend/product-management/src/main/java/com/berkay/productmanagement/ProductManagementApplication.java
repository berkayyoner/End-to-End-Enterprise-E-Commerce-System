package com.berkay.productmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductManagementApplication.class, args);
    }
}
