package com.inno.task.userservice.controller;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UpdateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import com.inno.task.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //create
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponseDto user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //find by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //find all
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(null, pageable));
    }

    //updt
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    //actv/deactv
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> setActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        userService.setActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}