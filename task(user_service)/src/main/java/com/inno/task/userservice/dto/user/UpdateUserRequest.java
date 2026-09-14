package com.inno.task.userservice.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 100, message = "Surname must be less than 100 characters")
    private String surname;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Email(message = "Invalid email format")
    private String email;
}