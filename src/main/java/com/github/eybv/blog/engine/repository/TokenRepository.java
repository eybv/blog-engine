package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.Token;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Token> tokenRowMapper = resultSet -> new Token(
            resultSet.getString("token"),
            resultSet.getLong("user_id"),
            resultSet.getTimestamp("created").toInstant(),
            resultSet.getTimestamp("last_used").toInstant()
    );

    public Optional<Token> findByValue(String value) {
        // language=PostgreSQL
        final var query = "SELECT token, user_id, created, last_used FROM tokens WHERE token = ?";
        return jdbcTemplate.single(query, tokenRowMapper, value);
    }

    public List<Token> findAllByUserId(long userId) {
        // language=PostgreSQL
        final var query = "SELECT token, user_id, created, last_used FROM tokens WHERE user_id = ?";
        return jdbcTemplate.query(query, tokenRowMapper, userId);
    }

    public Token updateLastUsed(Token token) {
        // language=PostgreSQL
        final var query = """
            UPDATE tokens SET last_used = DEFAULT WHERE token = ?
            RETURNING token, user_id, created, last_used
        """;
        return jdbcTemplate.single(query, tokenRowMapper, token.getValue()).orElseThrow();
    }

    public Token save(Token token) {
        // language=PostgreSQL
        final var query = """
            INSERT INTO tokens (token, user_id) VALUES (?, ?)
            RETURNING token, user_id, created, last_used
        """;
        return jdbcTemplate.single(query, tokenRowMapper, token.getValue(), token.getUserId()).orElseThrow();
    }

    public void remove(Token token) {
        // language=PostgreSQL
        final var query = "DELETE FROM tokens WHERE token = ?";
        jdbcTemplate.execute(query, token.getValue());
    }

}
