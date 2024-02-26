package com.example.msscbrewerygateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local-discovery")
@Configuration
public class LocalhostRouteConfig {

  @Bean
  public RouteLocator localhostRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("beer-service", r -> r.path("/api/v1/beer/*", "/api/v1/beerUpc/*", "/api/v1/beer*")
            .uri("http://localhost:8080"))
        .route("order-service", r -> r.path("/api/v1/customers/**")
            .uri("http://localhost:8081"))
        .route("inventory-service", r -> r.path("/api/v1/beer/*/inventory")
            .uri("http://localhost:8082"))
        .build();
  }
}
