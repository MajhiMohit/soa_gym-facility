package com.pulsefit.subscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * PulseFit Subscription Service
 * Manages membership plans, expiry dates, and renewal logic.
 * Uses OpenFeign to communicate with Member Service.
 * Registers with Eureka for service discovery.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SubscriptionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriptionServiceApplication.class, args);
    }
}
