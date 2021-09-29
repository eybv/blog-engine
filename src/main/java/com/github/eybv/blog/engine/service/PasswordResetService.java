package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.dto.UserData;
import com.github.eybv.blog.engine.dto.UserWithToken;
import com.github.eybv.blog.engine.exception.PasswordResetException;
import com.github.eybv.blog.engine.model.PasswordResetCode;
import com.github.eybv.blog.engine.repository.PasswordResetCodeRepository;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetService {

    private final long RESET_CODE_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 5;

    private final UserService userService;

    private final PasswordResetCodeRepository passwordResetCodeRepository;

    public String generateCode(String username) {
        final var userId = userService.findByUsername(username)
                .map(UserData::getId)
                .orElseThrow(PasswordResetException::new);
        Optional<PasswordResetCode> passwordResetCode;
        if (passwordResetCodeRepository.findByUserId(userId).isPresent()) {
            passwordResetCode = passwordResetCodeRepository.renewForUserId(userId);
        } else {
            passwordResetCode = passwordResetCodeRepository.createForUserId(userId);
        }
        return passwordResetCode.orElseThrow().getValue().toString();
    }

    public UserWithToken changePassword(String username, String code, String password) {
        try {
            final var userId = userService.findByUsername(username)
                    .map(UserData::getId)
                    .orElseThrow(PasswordResetException::new);
            return passwordResetCodeRepository.findByUserId(userId)
                    .flatMap(this::removeIfExpired)
                    .flatMap(x -> checkEqualsAndRemoveCode(x, code))
                    .map(x -> userService.changeUserPassword(userId, password))
                    .orElseThrow();
        } catch (Exception e) {
            throw new PasswordResetException();
        }
    }

    private Optional<PasswordResetCode> removeIfExpired(PasswordResetCode code) {
        final var now = Instant.now().toEpochMilli();
        final var created = code.getCreated().toEpochMilli();
        if (now - created > RESET_CODE_EXPIRATION_TIME_IN_SECONDS * 1000) {
            passwordResetCodeRepository.remove(code);
            return Optional.empty();
        }
        return Optional.of(code);
    }

    private Optional<PasswordResetCode> checkEqualsAndRemoveCode(PasswordResetCode code, String rawCode) {
        try {
            if (code.getValue().equals(UUID.fromString(rawCode))) {
                return Optional.of(code);
            }
            return Optional.empty();
        } finally {
            passwordResetCodeRepository.remove(code);
        }
    }

}
