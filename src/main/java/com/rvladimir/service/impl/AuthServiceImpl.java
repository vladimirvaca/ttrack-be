package com.rvladimir.service.impl;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.TokenResponseDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.security.token.validator.RefreshTokenValidator;

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
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenValidator refreshTokenValidator;

    private static final String INVALID_CREDENTIALS_MSG = "Invalid email or password.";
    private static final String TOKEN_GENERATION_FAILED_MSG = "Failed to generate token";
    private static final String INVALID_REFRESH_TOKEN_MSG = "Invalid or expired refresh token";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String USER_ID_CLAIM = "userId";
    private static final String DUMMY_PASSWORD_HASH = BCrypt.hashpw("dummy-password", BCrypt.gensalt());

    public AuthServiceImpl(
        UserRepository userRepository,
        TokenGenerator tokenGenerator,
        RefreshTokenGenerator refreshTokenGenerator,
        RefreshTokenValidator refreshTokenValidator
    ) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.refreshTokenValidator = refreshTokenValidator;
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
    public TokenResponseDTO mobileLogin(LoginDTO loginDTO) {
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

        User user = userOpt.get();
        String accessToken = generateToken(user);

        Authentication authentication = Authentication.build(
            user.getEmail(),
            List.of(user.getRole().name()),
            Map.of(USER_ID_CLAIM, user.getId())
        );

        String refreshToken = refreshTokenGenerator.generate(authentication, user.getEmail())
            .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, TOKEN_GENERATION_FAILED_MSG));

        return new TokenResponseDTO(accessToken, BEARER_TOKEN_TYPE, refreshToken);
    }

    @Override
    public TokenResponseDTO refresh(String refreshToken) {
        Optional<String> usernameOpt = refreshTokenValidator.validate(refreshToken);
        if (usernameOpt.isEmpty()) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MSG);
        }

        String email = usernameOpt.get();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, INVALID_REFRESH_TOKEN_MSG));

        String newAccessToken = generateToken(user);

        Authentication authentication = Authentication.build(
            user.getEmail(),
            List.of(user.getRole().name()),
            Map.of(USER_ID_CLAIM, user.getId())
        );

        String newRefreshToken = refreshTokenGenerator.generate(authentication, user.getEmail())
            .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, TOKEN_GENERATION_FAILED_MSG));

        return new TokenResponseDTO(newAccessToken, BEARER_TOKEN_TYPE, newRefreshToken);
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Instant now = Instant.now();
        claims.put("sub", user.getEmail());
        claims.put("iat", now.getEpochSecond());

        claims.put(USER_ID_CLAIM, user.getId());
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
