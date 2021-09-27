package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.model.Token;
import com.github.eybv.blog.engine.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    private final StringKeyGenerator stringKeyGenerator;

    public Optional<Token> findByValue(String token) {
        return tokenRepository.findByValue(token);
    }

    public Token generateToken(long userId) {
        final var key = stringKeyGenerator.generateKey();
        return tokenRepository.save(new Token(key, userId, null, null));
    }

    public void invalidateToken(Token token) {
        tokenRepository.remove(token);
    }

    public Token updateLastUsed(Token token) {
        return tokenRepository.updateLastUsed(token);
    }

}
