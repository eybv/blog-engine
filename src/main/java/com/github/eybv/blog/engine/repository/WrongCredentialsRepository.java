package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WrongCredentialsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Boolean> longToBooleanRowMapper = resultSet -> resultSet.getLong(1) > 0;

    public boolean containsUsername(String username) {
        // language=PostgreSQL
        final var query = "SELECT COUNT(username) FROM wrong_usernames WHERE username = ?";
        return jdbcTemplate.single(query, longToBooleanRowMapper, username).orElseThrow();
    }

    public boolean containsPassword(String password) {
        // language=PostgreSQL
        final var query = "SELECT COUNT(password) FROM wrong_passwords WHERE password = ?";
        return jdbcTemplate.single(query, longToBooleanRowMapper, password).orElseThrow();
    }

}
