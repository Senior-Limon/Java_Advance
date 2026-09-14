package com.inno.task.service;

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
import com.inno.task.authentication_service.service.AuthService;
import com.inno.task.authentication_service.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private AuthenticateRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setPasswordHash("hashed_password");
        testUser.setRole(Role.USER);
        testUser.setActive(true);

        registerRequest = new RegisterRequest();
        registerRequest.setLogin("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setRole("USER");

        loginRequest = new AuthenticateRequest();
        loginRequest.setLogin("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString(), any(Role.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");

        TokenResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists() {
        when(userRepository.existsByLogin(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_Success() {
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), any(Role.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");

        TokenResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
    }

    @Test
    void login_BadCredentials() {
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_UserBlocked() {
        testUser.setActive(false);
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(testUser));
        assertThrows(UserBlockedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void refresh_InvalidToken() {
        when(jwtService.validate(anyString())).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> authService.refresh("invalid_token"));
    }

    @Test
    void validate_ValidToken() {
        when(jwtService.validate(anyString())).thenReturn(true);
        assertDoesNotThrow(() -> authService.validate("valid_token"));
    }

    @Test
    void validate_InvalidToken() {
        when(jwtService.validate(anyString())).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> authService.validate("invalid_token"));
    }
}