package com.inno.task.userservice.controller;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate; //client

    //create by API and see 201 Created
    @Test
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test");
        request.setSurname("User");
        request.setEmail("test@example.com");
        request.setBirthDate(java.time.LocalDate.of(2000, 1, 1));

        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
                "/api/users", request, UserResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Test", response.getBody().getName());
    }
}