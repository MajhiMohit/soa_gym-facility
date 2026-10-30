package com.pulsefit.auth.config;

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
 * Swagger / OpenAPI 3 configuration for Auth Service.
 * Accessible at: http://localhost:8083/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PulseFit Auth Service API")
                        .description("JWT Authentication & User Registration Service.\n\n" +
                                "**Flow:** Register → Login → Copy JWT → Use as Bearer token in other services.\n\n" +
                                "All endpoints in this service are **publicly accessible** (no JWT required).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PulseFit Team")
                                .email("support@pulsefit.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Direct (Auth Service)"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter the JWT token returned from /auth/login or /auth/register")));
        // Note: Auth endpoints are public, so no global SecurityRequirement is added
    }
}
