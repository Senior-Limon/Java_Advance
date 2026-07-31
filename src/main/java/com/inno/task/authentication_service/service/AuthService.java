package com.inno.task.authentication_service.service;

import com.inno.task.authentication_service.dto.AuthenticateRequest;
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

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully with role {}", savedUser.getLogin(), savedUser.getRole());

        return buildTokenResponse(savedUser);
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