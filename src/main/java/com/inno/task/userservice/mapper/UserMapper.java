package com.inno.task.userservice.mapper;

import com.inno.task.userservice.dto.user.CreateUserRequest;
import com.inno.task.userservice.dto.user.UpdateUserRequest;
import com.inno.task.userservice.dto.user.UserResponseDto;
import com.inno.task.userservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    //entity - response
    UserResponseDto toResponse(User user);

    //create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "paymentCards", ignore = true)
    User toEntity(CreateUserRequest request);

    //update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "paymentCards", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}