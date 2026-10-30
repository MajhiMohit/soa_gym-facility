package com.pulsefit.subscription.config;

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
 * Swagger / OpenAPI 3 configuration for Subscription Service.
 * Accessible at: http://localhost:8082/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI subscriptionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PulseFit Subscription Service API")
                        .description("Membership Subscription & Renewal Management Service.\n\n" +
                                "**Authentication:** All endpoints require a valid JWT Bearer token.\n" +
                                "Get a token from the Auth Service (`POST /auth/login`) and paste it below.\n\n" +
                                "**Prerequisite:** Create a Member first via the Member Service.\n\n" +
                                "**Base URL via Gateway:** `http://localhost:8080/api/subscriptions`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PulseFit Team")
                                .email("support@pulsefit.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Direct (Subscription Service)"),
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
