package com.rvladimir.service.impl;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.JwtDTO;
import com.rvladimir.service.dto.LoginDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.token.generator.TokenGenerator;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

@Singleton
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private static final String INVALID_CREDENTIALS_MSG = "Invalid email or password.";
    private static final String USER_NOT_FOUND_MSG = "User not found.";

    public AuthServiceImpl(UserRepository userRepository, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public JwtDTO login(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByEmail(loginDTO.getEmail());
        if (userOpt.isEmpty()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, USER_NOT_FOUND_MSG);
        }

        User user = userOpt.get();
        boolean passwordMatches = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MSG);
        }

        Map<String, Object> claims = new HashMap<>();
        Instant now = Instant.now();
        claims.put("sub", user.getEmail());
        claims.put("iat", now.getEpochSecond());

        claims.put("userId", user.getId());
        claims.put("roles", List.of(user.getRole().name()));

        String token = tokenGenerator.generateToken(claims)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token"));

        return new JwtDTO(token);
    }
}
