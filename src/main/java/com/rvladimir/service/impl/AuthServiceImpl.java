package com.rvladimir.service.impl;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.AuthService;
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
    private static final String TOKEN_GENERATION_FAILED_MSG = "Failed to generate token";
    private static final String DUMMY_PASSWORD_HASH = BCrypt.hashpw("dummy-password", BCrypt.gensalt());

    public AuthServiceImpl(UserRepository userRepository, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        String email = normalizeEmail(loginDTO.getEmail());
        if (email == null || email.isBlank()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MSG);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        String storedHash = userOpt.map(User::getPassword).orElse(DUMMY_PASSWORD_HASH);
        boolean passwordMatches = BCrypt.checkpw(loginDTO.getPassword(), storedHash);

        if (userOpt.isEmpty() || !passwordMatches) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MSG);
        }

        return generateToken(userOpt.get());
    }

    @Override
    public String refreshLogin(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, USER_NOT_FOUND_MSG);
        }

        return generateToken(userOpt.get());
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Instant now = Instant.now();
        claims.put("sub", user.getEmail());
        claims.put("iat", now.getEpochSecond());

        claims.put("userId", user.getId());
        claims.put("roles", List.of(user.getRole().name()));

        return tokenGenerator.generateToken(claims)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, TOKEN_GENERATION_FAILED_MSG));
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }

        return email.trim();
    }
}
