package com.pulsefit.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * PulseFit Member Service
 * Manages member profiles (CRUD) for the fitness platform.
 * Registers with Eureka for service discovery.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MemberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
    }
}
