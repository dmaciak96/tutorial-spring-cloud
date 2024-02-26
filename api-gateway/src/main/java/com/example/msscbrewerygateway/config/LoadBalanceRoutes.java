package com.example.msscbrewerygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@Configuration
public class LoadBalanceRoutes {

  @Bean
  public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("beer-service", r -> r.path("/api/v1/beer/*", "/api/v1/beerUpc/*", "/api/v1/beer*")
            .uri("lb://beer-service"))
        .route("order-service", r -> r.path("/api/v1/customers/**")
            .uri("lb://order-service"))
        .route("inventory-service", r -> r.path("/api/v1/beer/*/inventory")
            .filters(f -> f.circuitBreaker(c -> c
                .setName("inventory-CB")
                .setFallbackUri("forward:/inventory-failover")
                .setRouteId("inv-fallback")))
            .uri("lb://inventory-service"))
        .route("inventory-failover", r -> r.path("/inventory-failover/**")
            .uri("lb://inventory-failover"))
        .build();
  }
}
