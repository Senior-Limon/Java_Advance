package com.inno.task.authentication_service.service;

import com.inno.task.authentication_service.dto.AuthenticateRequest;
import com.inno.task.authentication_service.dto.CreateUserRequest;
import com.inno.task.authentication_service.dto.RegisterRequest;
import com.inno.task.authentication_service.dto.TokenResponse;
import com.inno.task.authentication_service.entity.Role;
import com.inno.task.authentication_service.entity.User;
import com.inno.task.authentication_service.exception.InvalidTokenException;
import com.inno.task.authentication_service.exception.UserAlreadyExistsException;
import com.inno.task.authentication_service.exception.UserBlockedException;
import com.inno.task.authentication_service.mapper.UserMapper;
import com.inno.task.authentication_service.repository.UserRepository;
import com.inno.task.authentication_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestClient restClient;

    @Transactional(readOnly = true)
    public TokenResponse login(AuthenticateRequest request) {
        log.info("Login attempt for user: {}", request.getLogin());

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        if (!user.getActive()) {
            throw new UserBlockedException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid login or password");
        }

        log.info("User {} logged in successfully", user.getLogin());
        return buildTokenResponse(user);
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("Registration attempt for login: {}", request.getLogin());

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException("Login '" + request.getLogin() + "' is already taken");
        }

        User authUser = userMapper.toEntity(request);
        authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authUser.setActive(true);
        userRepository.save(authUser);

        try {
            CreateUserRequest profileRequest = new CreateUserRequest();
            profileRequest.setEmail(request.getLogin()); // email = login
            profileRequest.setName(request.getLogin());

            restClient.post()
                    .uri("/internal/users")
                    .body(profileRequest)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Profile created in User Service for login: {}", request.getLogin());

        } catch (Exception e) {
            log.error("Failed to create profile in User Service for login: {}. Rolling back.",
                    request.getLogin(), e);
            throw new RuntimeException("Failed to sync with User Service", e);
        }

        log.info("User {} registered successfully", authUser.getLogin());
        return buildTokenResponse(authUser);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        log.info("Token refresh attempt");

        if (!jwtService.validate(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new InvalidTokenException("User not found for refresh token"));

        if (!user.getActive()) {
            throw new UserBlockedException("Cannot refresh token for deactivated user");
        }

        log.info("Token refreshed for user {}", user.getLogin());
        return buildTokenResponse(user);
    }

    @Transactional(readOnly = true)
    public void validate(String token) {
        if (!jwtService.validate(token)) {
            throw new InvalidTokenException("Access token is invalid or expired");
        }
    }

    //helper method, to avoid duplicating the token generation code
    private TokenResponse buildTokenResponse(User user) {
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user.getLogin(), user.getRole()))
                .refreshToken(jwtService.generateRefreshToken(user.getLogin()))
                .build();
    }
}