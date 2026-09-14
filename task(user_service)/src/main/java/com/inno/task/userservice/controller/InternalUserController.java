package com.inno.task.userservice.controller;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import com.inno.task.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createInternal(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(req));
    }
}