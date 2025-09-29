package com.self.appreciation.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("user_service", r -> r.path("/user/**")
//                        .uri("lb://user-service"))
//                .route("order_service", r -> r.path("/order/**")
//                        .uri("lb://order-service"))
//                .build();
//    }

    // 自定义过滤器

    // 全局异常处理

    // 自定义负载均衡策略

    // 监控和指标收集
}
