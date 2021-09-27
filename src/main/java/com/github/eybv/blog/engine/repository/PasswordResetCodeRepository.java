package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.PasswordResetCode;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetCodeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PasswordResetCode> resetCodeRowMapper = resultSet -> new PasswordResetCode(
            resultSet.getLong("id"),
            UUID.fromString(resultSet.getString("code")),
            resultSet.getLong("user_id"),
            resultSet.getTimestamp("created").toLocalDateTime()
    );

    public Optional<PasswordResetCode> findByUserId(long userId) {
        // language=PostgreSQL
        final var query = "SELECT id, code, user_id, created FROM password_reset_codes WHERE user_id = ?";
        return jdbcTemplate.single(query, resetCodeRowMapper, userId);
    }

    public Optional<PasswordResetCode> createForUserId(long userId) {
        // language=PostgreSQL
        final var query = """
            INSERT INTO password_reset_codes (user_id) VALUES (?)
            RETURNING id, code, user_id, created
        """;
        return jdbcTemplate.single(query, resetCodeRowMapper, userId);
    }

    public Optional<PasswordResetCode> renewForUserId(long userId) {
        // language=PostgreSQL
        final var query = """
            UPDATE password_reset_codes SET code = DEFAULT, created = DEFAULT WHERE user_id = ?
            RETURNING id, code, user_id, created
        """;
        return jdbcTemplate.single(query, resetCodeRowMapper, userId);
    }

    public void remove(PasswordResetCode resetCode) {
        // language=PostgreSQL
        final var query = "DELETE FROM password_reset_codes WHERE id = ?";
        jdbcTemplate.execute(query, resetCode.getId());
    }

}
