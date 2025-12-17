package com.rvladimir.service.mapper;

import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import io.micronaut.context.annotation.Mapper;
import io.micronaut.core.annotation.Introspected;

import jakarta.inject.Singleton;

@Singleton
@Introspected
public abstract class UserMapper {

    /**
     * Convert CreateUserDTO to User entity
     * @param createUserDTO the DTO to convert
     * @return the User entity
     */
    @Mapper
    public abstract User toEntity(CreateUserDTO createUserDTO);

    /**
     * Convert UserDTO to User entity
     *
     * @param userDTO the DTO to convert
     * @return the User entity
     */
    @Mapper
    public abstract User toEntity(UserDTO userDTO);

    /**
     * Convert User entity to UserDTO
     * @param user the entity to convert
     * @return the UserDTO
     */
    @Mapper
    public abstract UserDTO toDto(User user);
}
