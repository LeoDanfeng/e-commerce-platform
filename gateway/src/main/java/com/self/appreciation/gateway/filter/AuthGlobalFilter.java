// gateway/src/main/java/com/self/appreciation/gateway/filter/AuthGlobalFilter.java

package com.self.appreciation.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 放过登录和注册接口
        if (request.getURI().getPath().contains("/api/auth")) {
            return chain.filter(exchange);
        }

        // 从请求头获取token
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String token = authHeader.substring(7);

        // 构建调用user服务的URL，使用lb://前缀
        String userServiceUrl = "lb://user-service/api/auth/validate-token";

        return webClientBuilder.build()
                .post()
                .uri(userServiceUrl)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    log.warn("Token无效或已过期");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return Mono.error(new RuntimeException("Token无效或已过期"));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    log.error("验证Token时服务器内部错误");
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return Mono.error(new RuntimeException("验证Token时服务器内部错误"));
                })
                .bodyToMono(Void.class)
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1; // 设置为最高优先级
    }
}
