package com.rvladimir.service.impl;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.UserService;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;
import com.rvladimir.service.mapper.UserMapper;
import com.rvladimir.web.error.ValidationException;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;

@Singleton
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final int BCRYPT_COST = 12;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO create(CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new ValidationException("Duplicate value for email", "email", "DUPLICATE");
        }

        User user = userMapper.toEntity(createUserDTO);
        // Hash the user's password before persisting (BCrypt with a cost factor)
        String hashedPassword = BCrypt.hashpw(createUserDTO.getPassword(), BCrypt.gensalt(BCRYPT_COST));
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
