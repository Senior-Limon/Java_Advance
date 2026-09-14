package com.inno.task.authentication_service.controller;

import com.inno.task.authentication_service.dto.AuthenticateRequest;
import com.inno.task.authentication_service.dto.RegisterRequest;
import com.inno.task.authentication_service.dto.TokenRefreshRequest;
import com.inno.task.authentication_service.dto.TokenResponse;
import com.inno.task.authentication_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthenticateRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestParam String token) {
        authService.validate(token);
        return ResponseEntity.ok().build();
    }
}