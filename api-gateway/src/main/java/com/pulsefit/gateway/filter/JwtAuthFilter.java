package com.pulsefit.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Global JWT Authentication Filter for Spring Cloud Gateway.
 *
 * Intercepts ALL incoming requests and:
 *  - Skips validation for public routes (/auth/**)
 *  - Validates Bearer JWT token for all protected routes
 *  - Rejects requests with missing, invalid, or expired tokens (401)
 *  - Forwards validated username/role as headers to downstream services
 *
 * Runs as a GlobalFilter with highest precedence (Ordered.HIGHEST_PRECEDENCE).
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Routes that bypass JWT authentication.
     */
    private static final List<String> OPEN_ROUTES = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/health",
            "/actuator",
            // Swagger / OpenAPI docs - allow unauthenticated access
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars",
            "/auth-service/v3/api-docs",
            "/member-service/v3/api-docs",
            "/subscription-service/v3/api-docs",
            "/attendance-service/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow public routes without JWT validation
        if (isOpenRoute(path)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rejectWithUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            // Parse and validate token using JJWT 0.12.x API
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check token expiry
            if (claims.getExpiration().before(new Date())) {
                return rejectWithUnauthorized(exchange, "JWT token has expired");
            }

            String username = claims.getSubject();
            String role     = claims.get("role", String.class);

            // Forward user info to downstream services as request headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Auth-Username", username != null ? username : "")
                    .header("X-Auth-Role",     role     != null ? role     : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return rejectWithUnauthorized(exchange, "Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * Return 401 Unauthorized response.
     */
    private Mono<Void> rejectWithUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);
        var buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Check if the request path matches any open (public) route.
     */
    private boolean isOpenRoute(String path) {
        return OPEN_ROUTES.stream().anyMatch(path::startsWith);
    }

    /**
     * Build the HMAC signing key from the configured secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
