package com.self.appreciation.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Oauth2Controller {

    private final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    private final ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService;

    private final ReactiveOAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient;

    @GetMapping("/login")
    public Object index(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("oauth2Client", authorizedClient.getClientRegistration().getRegistrationId());
        map.put("principal", oauth2User);
        map.put("accessToken", authorizedClient.getAccessToken().getTokenValue());
        map.put("refreshToken", authorizedClient.getRefreshToken().getTokenValue());
        return map;
    }

    @GetMapping("/")
    public Object index(Authentication authentication) {
        return authentication.getPrincipal();
    }

    @GetMapping("/user-info")
    public Object getUserInfo(Authentication authentication) {
        log.info("authentication: {}", authentication.getClass().getName());
        return authentication;
    }

    @GetMapping("/access-token/login-client")
    public Object accessToken(@RegisteredOAuth2AuthorizedClient("login-client") OAuth2AuthorizedClient authorizedClient,
                              Authentication authentication) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("principal", authentication.getPrincipal());
        map.put("accessToken", authorizedClient.getAccessToken().getTokenValue());
        map.put("refreshToken", authorizedClient.getRefreshToken().getTokenValue());
        return map;
    }

    @PostMapping("/refresh-token")
    public Mono<Map<String, Object>> refreshToken(ServerWebExchange webExchange) {
        return webExchange.getFormData()
                .flatMap(formData -> {
                    String accessToken = formData.getFirst("accessToken");
                    String refreshToken = formData.getFirst("refreshToken");
                    String principalName = formData.getFirst("principalName");
                    return Flux.fromIterable(() -> {
                                InMemoryReactiveClientRegistrationRepository repo = (InMemoryReactiveClientRegistrationRepository) reactiveClientRegistrationRepository;
                                return repo.iterator();
                            })
                            .flatMap(clientRegistration ->
                                    reactiveOAuth2AuthorizedClientService
                                            .loadAuthorizedClient(clientRegistration.getRegistrationId(), principalName)
                                            .filter(Objects::nonNull) // 过滤掉 null 值
                                            .flatMap(authorizedClient -> {
                                                // 检查令牌是否匹配
                                                if (!authorizedClient.getAccessToken().getTokenValue().equals(accessToken) ||
                                                        !authorizedClient.getRefreshToken().getTokenValue().equals(refreshToken)) {
                                                    return Mono.empty(); // 不匹配则跳过
                                                }

                                                // 正确构造 OAuth2RefreshTokenGrantRequest
                                                OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest =
                                                        new OAuth2RefreshTokenGrantRequest(
                                                                clientRegistration,
                                                                authorizedClient.getAccessToken(),
                                                                authorizedClient.getRefreshToken()
                                                        );

                                                // 执行刷新令牌请求
                                                return accessTokenResponseClient.getTokenResponse(refreshTokenGrantRequest)
                                                        .flatMap(tokenResponse -> {
                                                            // 创建新的 OAuth2AuthorizedClient 对象
                                                            OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(
                                                                    clientRegistration,
                                                                    authorizedClient.getPrincipalName(),
                                                                    tokenResponse.getAccessToken(),
                                                                    tokenResponse.getRefreshToken()
                                                            );

                                                            // 保存更新后的 OAuth2AuthorizedClient
                                                            return reactiveOAuth2AuthorizedClientService.saveAuthorizedClient(
                                                                    updatedAuthorizedClient,
                                                                    // 如果需要 Authentication 对象，可以通过其他方式获取
                                                                    null // 或传入实际的 Authentication 对象
                                                            ).then(Mono.just(tokenResponse));
                                                        });
                                            })
                            )
                            .next() // 获取第一个匹配的结果
                            .map(tokenResponse -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("accessToken", tokenResponse.getAccessToken().getTokenValue());
                                if (tokenResponse.getRefreshToken() != null) {
                                    map.put("refreshToken", tokenResponse.getRefreshToken().getTokenValue());
                                }
                                return map;
                            })
                            .switchIfEmpty(Mono.just(Map.of("error", "No matching authorized client found")))
                            .onErrorReturn(Map.of("error", "Failed to refresh token"));
                });
    }
}
