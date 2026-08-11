package com.inno.task.authentication_service.mapper;

import com.inno.task.authentication_service.dto.RegisterRequest;
import com.inno.task.authentication_service.entity.Role;
import com.inno.task.authentication_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "role", target = "role", qualifiedByName = "mapRole")
    User toEntity(RegisterRequest request);

    @Named("mapRole")
    default Role mapRole(String role) {
        if (role == null) return null;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Must be ADMIN or USER");
        }
    }
}