package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.UserRole;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRoleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserRole> roleRowMapper = resultSet ->
            UserRole.valueOf(resultSet.getString("name"));

    public List<UserRole> findAll() {
        // language=PostgreSQL
        final var query = "SELECT name FROM roles";
        return jdbcTemplate.query(query, roleRowMapper);
    };

    public List<UserRole> findAllAssignedToUser(long userId) {
        // language=PostgreSQL
        final var query = """
            SELECT name FROM roles WHERE id IN (
                SELECT role_id FROM user_roles WHERE user_id = ?
            )
        """;
        return jdbcTemplate.query(query, roleRowMapper, userId);
    }

    public List<UserRole> assignToUser(UserRole role, long userId) {
        // language=PostgreSQL
        final var query = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        jdbcTemplate.execute(query, userId, role.getId());
        return findAllAssignedToUser(userId);
    }

    public List<UserRole> removeAssignToUser(UserRole role, long userId) {
        // language=PostgreSQL
        final var query = "DELETE FROM user_roles WHERE user_id =? AND role_id = ?";
        jdbcTemplate.execute(query, userId, role.getId());
        return findAllAssignedToUser(userId);
    }

}
