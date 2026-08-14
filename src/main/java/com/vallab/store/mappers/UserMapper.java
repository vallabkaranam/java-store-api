package com.vallab.store.mappers;

import com.vallab.store.dtos.RegisterUserRequest;
import com.vallab.store.dtos.UserDto;
import com.vallab.store.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);
}