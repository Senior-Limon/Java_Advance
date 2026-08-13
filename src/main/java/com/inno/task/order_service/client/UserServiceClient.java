package com.inno.task.order_service.client;

import com.inno.task.order_service.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestClient restClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public UserDto getUserById(Long userId) {
        log.info("Fetching user info for id: {}", userId);
        return restClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(UserDto.class);
    }

    private UserDto getUserFallback(Long userId, Throwable t) {
        log.warn("Failed to fetch user info for id {}. Returning fallback.", userId, t);
        return UserDto.builder()
                .id(userId)
                .name("Unknown User")
                .email("unknown@example.com")
                .build();
    }
}