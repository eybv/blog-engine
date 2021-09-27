package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.Category;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = resultSet -> new Category(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public Optional<Category> findById(long id) {
        // language=PostgreSQL
        final var query = "SELECT id, name FROM categories WHERE id = ?";
        return jdbcTemplate.single(query, categoryRowMapper, id);
    }

    public Optional<Category> findByName(String name) {
        // language=PostgreSQL
        final var query = "SELECT id, name FROM categories WHERE name = ?";
        return jdbcTemplate.single(query, categoryRowMapper, name);
    }

    public List<Category> findAll(long limit, long offset) {
        // language=PostgreSQL
        final var query = "SELECT id, name FROM categories LIMIT ? OFFSET ?";
        return jdbcTemplate.query(query, categoryRowMapper, limit, offset);
    }

    public Category save(Category category) {
        return category.getId() == 0 ? insert(category) : update(category);
    }

    public void remove(Category category) {
        // language=PostgreSQL
        final var query = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.execute(query, category.getId());
    }

    private Category insert(Category category) {
        // language=PostgreSQL
        final var query = "INSERT INTO categories (name) VALUES (?) RETURNING id, name";
        return jdbcTemplate.single(query, categoryRowMapper, category.getName()).orElseThrow();
    }

    private Category update(Category category) {
        // language=PostgreSQL
        final var query = "UPDATE categories SET name = ? WHERE id = ? RETURNING id, name";
        return jdbcTemplate.single(query, categoryRowMapper, category.getName(), category.getId()).orElseThrow();
    }

}
