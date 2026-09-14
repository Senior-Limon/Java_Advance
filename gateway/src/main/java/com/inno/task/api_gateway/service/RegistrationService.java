package com.inno.task.api_gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${USER_SERVICE_URL:http://localhost:8082}")
    private String userServiceUrl;

    public Mono<Map<String, Object>> register(Map<String, Object> request) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/auth/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(authResponse -> {
                    log.info("Auth credentials created: {}", authResponse);

                    String userId = authResponse.get("userId").toString();
                    Map<String, Object> userData = Map.of(
                            "userId", userId,
                            "email", request.get("email"),
                            "name", request.get("name")
                    );

                    return webClientBuilder.build()
                            .post()
                            .uri(userServiceUrl + "/api/users")
                            .bodyValue(userData)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(userResponse -> {
                                log.info("User data created: {}", userResponse);
                                return Map.of(
                                        "status", "success",
                                        "auth", authResponse,
                                        "user", userResponse
                                );
                            })
                            .onErrorResume(userError -> {
                                log.error("User service failed, rolling back auth. Error: {}",
                                        userError.getMessage());
                                return rollbackAuth(userId)
                                        .then(Mono.<Map<String, Object>>error(
                                                new RuntimeException(
                                                        "Registration failed: " + userError.getMessage())));
                            });
                });
    }

    private Mono<Void> rollbackAuth(String userId) {
        log.warn("Rolling back auth credentials for userId: {}", userId);
        return webClientBuilder.build()
                .delete()
                .uri(authServiceUrl + "/api/auth/users/" + userId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Auth rolled back for userId: {}", userId))
                .doOnError(e -> log.error("Failed to rollback auth for userId: {}", userId, e))
                .then();
    }
}