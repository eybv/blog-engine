package com.github.eybv.blog.core.jdbc;

import com.github.eybv.blog.core.annotation.Component;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JdbcTemplate {

    private interface Executor<T> {
        T execute(PreparedStatement statement) throws SQLException;
    }

    private final DataSource dataSource;

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return execute(sql, args, statement -> {
            try (final var resultSet = statement.executeQuery()) {
                final var result = new ArrayList<T>();
                while (resultSet.next()) {
                    result.add(rowMapper.map(resultSet));
                }
                return result;
            }
        });
    }

    public <T> Optional<T> single(String sql, RowMapper<T> rowMapper, Object... args) {
        return query(sql, rowMapper, args).stream().findFirst();
    }

    public void execute(String sql, Object... args) {
        execute(sql, args, PreparedStatement::executeUpdate);
    }

    private <T> T execute(String sql, Object[] args, Executor<T> executor) {
        try (
                final var connection = dataSource.getConnection();
                final var statement = connection.prepareStatement(sql)
        ) {

            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }

            return executor.execute(statement);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

}
