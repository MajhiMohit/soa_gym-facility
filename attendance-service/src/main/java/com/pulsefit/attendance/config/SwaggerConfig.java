package com.pulsefit.attendance.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 3 configuration for Attendance Service.
 * Accessible at: http://localhost:8084/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI attendanceServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PulseFit Attendance Service API")
                        .description("Member Check-In & Attendance Tracking Service.\n\n" +
                                "**Authentication:** All endpoints require a valid JWT Bearer token.\n" +
                                "Get a token from the Auth Service (`POST /auth/login`) and paste it below.\n\n" +
                                "**Prerequisites:**\n" +
                                "1. Create a Member (Member Service)\n" +
                                "2. Create an active Subscription for that Member (Subscription Service)\n" +
                                "3. Then check in the Member here\n\n" +
                                "**Base URL via Gateway:** `http://localhost:8080/api/attendance`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PulseFit Team")
                                .email("support@pulsefit.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Direct (Attendance Service)"),
                        new Server().url("http://localhost:8080").description("Via API Gateway (JWT required)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter the JWT token from Auth Service /auth/login")));
    }
}
