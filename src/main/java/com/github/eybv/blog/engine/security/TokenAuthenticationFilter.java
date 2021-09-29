package com.github.eybv.blog.engine.security;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.Order;
import com.github.eybv.blog.core.request.RequestAttribute;
import com.github.eybv.blog.core.security.Authentication;
import com.github.eybv.blog.engine.model.Token;
import com.github.eybv.blog.engine.service.TokenService;
import com.github.eybv.blog.engine.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Order(1)
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends HttpFilter {

    private final long TOKEN_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 15;

    private final TokenService tokenService;
    private final UserService userService;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final var existingAuthentication = (Authentication) req.getAttribute(RequestAttribute.AUTHENTICATION);
        if (existingAuthentication == null || !existingAuthentication.isAuthenticated()) {
             Optional.ofNullable(req.getHeader("Authorization"))
                     .filter(header -> header.toLowerCase(Locale.ROOT).trim().startsWith("bearer"))
                     .map(header -> header.substring(7))
                     .flatMap(tokenService::findByValue)
                     .flatMap(this::removeIfExpired)
                     .map(tokenService::updateLastUsed)
                     .flatMap(token -> userService.findById(token.getUserId()))
                     .ifPresent(user -> {
                         final var authentication = new SimpleAuthenticationToken(user.getId(), user.getRoles());
                         authentication.setAuthenticated(user.isActive());
                         req.setAttribute(RequestAttribute.AUTHENTICATION, authentication);
                     });
        }
        super.doFilter(req, res, chain);
    }

    private Optional<Token> removeIfExpired(Token token) {
        final var now = Instant.now().toEpochMilli();
        final var created = token.getCreated().toEpochMilli();
        if (now - created > TOKEN_EXPIRATION_TIME_IN_SECONDS * 1000) {
            tokenService.invalidateToken(token);
            return Optional.empty();
        }
        return Optional.of(token);
    }

}
