package com.rvladimir.service.mapper;

import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import jakarta.inject.Singleton;

@Singleton
public class UserMapper {

    /**
     * Convert CreateUserDTO to User entity
     * @param createUserDTO the DTO to convert
     * @return the User entity
     */
    public User toEntity(CreateUserDTO createUserDTO) {
        if (createUserDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(null);
        user.setName(createUserDTO.getName());
        user.setLastname(createUserDTO.getLastname());
        user.setDateBirth(createUserDTO.getDateBirth());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(createUserDTO.getPassword());
        user.setRole(createUserDTO.getRole());
        return user;
    }

    /**
     * Convert UserDTO to User entity
     *
     * @param userDTO the DTO to convert
     * @return the User entity
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(null);
        user.setName(userDTO.getName());
        user.setLastname(userDTO.getLastname());
        user.setDateBirth(userDTO.getDateBirth());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        return user;
    }

    /**
     * Convert User entity to UserDTO
     * @param user the entity to convert
     * @return the UserDTO
     */
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getDateBirth(),
            user.getEmail(),
            user.getRole()
        );
    }
}
