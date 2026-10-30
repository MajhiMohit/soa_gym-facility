package com.pulsefit.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * PulseFit Attendance Service
 * Tracks member check-ins. Validates membership via Subscription Service
 * and member existence via Member Service before logging attendance.
 * Registers with Eureka for service discovery.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AttendanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServiceApplication.class, args);
    }
}
