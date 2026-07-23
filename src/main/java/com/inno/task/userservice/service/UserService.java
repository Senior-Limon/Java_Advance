package com.inno.task.userservice.service;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UpdateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import com.inno.task.userservice.entity.User;
import com.inno.task.userservice.exception.NotFoundException;
import com.inno.task.userservice.mapper.UserMapper;
import com.inno.task.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String USER_CACHE = "users";

    //create
    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#request.email") // Ключ по email — уникальный
    public UserResponseDto createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
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

    //update
    @Transactional
    @CacheEvict(value = USER_CACHE, key = "#id")
    public UserResponseDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userMapper.updateEntity(request, user);
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
    }
}