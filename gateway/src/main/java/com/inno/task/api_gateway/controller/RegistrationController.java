package com.inno.task.api_gateway.controller;

import com.inno.task.api_gateway.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono; 

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, Object>>> register(@RequestBody Map<String, Object> request) {
        return registrationService.register(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Registration failed: " + e.getMessage());
                    return Mono.just(
                            ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))
                    );
                });
    }
}