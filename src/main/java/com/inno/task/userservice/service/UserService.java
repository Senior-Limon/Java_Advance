package com.inno.task.userservice.service;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UpdateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import com.inno.task.userservice.entity.User;
import com.inno.task.userservice.exception.NotFoundException;
import com.inno.task.userservice.mapper.UserMapper;
import com.inno.task.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_CACHE = "users";

    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#request.email")
    public UserResponseDto createUser(CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(userRepository.save(user));
    }

    //read by id
    @Cacheable(value = USER_CACHE, key = "#id")
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    //read all
    public Page<UserResponseDto> getAllUsers(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#id")
    public UserResponseDto updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userMapper.updateEntity(request, user);
        log.info("User {} updated successfully", id);
        return userMapper.toResponse(userRepository.save(user));
    }

    //actv/deactv
    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#id")
    public void setActiveStatus(Long id, boolean active) {
        int updated = userRepository.setActiveStatus(id, active);
        if (updated == 0) {
            throw new NotFoundException("User not found with id: " + id);
        }
        log.info(active ? "activate user with id: {}" : "deactivate user with id: {}", id);
    }


    //new helper method for payment-card service because old method return a response dto
    @Cacheable(value = USER_CACHE, key = "#userId")
    public User getUserEntityById(Long userId) {
        log.debug("Fetching user entity by id: {} for internal use", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}