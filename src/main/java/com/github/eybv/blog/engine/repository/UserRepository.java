package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.User;
import com.github.eybv.blog.engine.model.UserWithPassword;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = resultSet -> new User(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getBoolean("active")
    );

    private final RowMapper<UserWithPassword> userWithPasswordRowMapper = resultSet ->
            new UserWithPassword(new User(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getBoolean("active")
            ),
            resultSet.getString("password")
    );

    public Optional<UserWithPassword> findById(long id) {
        // language=PostgreSQL
        final var query = "SELECT id, username, active, password FROM users WHERE id = ?";
        return jdbcTemplate.single(query, userWithPasswordRowMapper, id);
    }

    public Optional<UserWithPassword> findByUsername(String username) {
        // language=PostgreSQL
        final var query = "SELECT id, username, active, password FROM users WHERE username = ?";
        return jdbcTemplate.single(query, userWithPasswordRowMapper, username);
    }

    public User save(UserWithPassword user) {
        return user.getUser().getId() == 0 ? insert(user) : update(user);
    }

    private User insert(UserWithPassword userWithPassword) {
        // language=PostgreSQL
        final var query = """
            INSERT INTO users (username, password, active) VALUES (?, ?, ?)
            RETURNING id, username, active
        """;
        return jdbcTemplate.single(query, userRowMapper,
                userWithPassword.getUser().getUsername(),
                userWithPassword.getPassword(),
                userWithPassword.getUser().isActive()
        ).orElseThrow();
    }

    private User update(UserWithPassword userWithPassword) {
        // language=PostgreSQL
        final var query = """
            UPDATE users SET active = ?, password = ? WHERE id = ?
            RETURNING id, username, active
        """;
        return jdbcTemplate.single(query, userRowMapper,
                userWithPassword.getUser().isActive(),
                userWithPassword.getPassword(),
                userWithPassword.getUser().getId()
        ).orElseThrow();
    }

}
