package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.Post;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> postRowMapper = resultSet -> new Post(
            resultSet.getLong("id"),
            resultSet.getLong("author_id"),
            resultSet.getLong("category_id"),
            resultSet.getString("title"),
            resultSet.getString("content"),
            resultSet.getTimestamp("created").toLocalDateTime()
    );

    public Optional<Post> findById(long id) {
        // language=PostgreSQL
        final var query = """
            SELECT id, author_id, category_id, title, content, created FROM posts
            WHERE id = ? AND deleted IS NULL
        """;
        return jdbcTemplate.single(query, postRowMapper, id);
    }

    public List<Post> findAll(long limit, long offset) {
        // language=PostgreSQL
        final var query = """
            SELECT id, author_id, category_id, title, content, created FROM posts
            WHERE deleted IS NULL
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(query, postRowMapper, limit, offset);
    }

    public List<Post> findAllByAuthorId(long authorId, long limit, long offset) {
        // language=PostgreSQL
        final var query = """
            SELECT id, author_id, category_id, title, content, created FROM posts
            WHERE author_id = ? AND deleted IS NULL
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(query, postRowMapper, authorId, limit, offset);
    }

    public List<Post> findAllByCategoryId(long categoryId, long limit, long offset) {
        // language=PostgreSQL
        final var query = """
            SELECT id, author_id, category_id, title, content, created FROM posts
            WHERE category_id = ? AND deleted IS NULL
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(query, postRowMapper, categoryId, limit, offset);
    }

    public Post save(Post post) {
        return post.getId() == 0 ? insert(post) : update(post);
    }

    public void remove(Post post) {
        // language=PostgreSQL
        final var query = "UPDATE posts SET deleted = current_timestamp WHERE id = ?";
        jdbcTemplate.execute(query, post.getId());
    }

    private Post insert(Post post) {
        // language=PostgreSQL
        final var query = """
            INSERT INTO posts (author_id, category_id, title, content) VALUES (?, ?, ?, ?)
            RETURNING id, author_id, category_id, title, content, created
        """;
        return jdbcTemplate.single(query, postRowMapper,
                post.getAuthorId(),
                post.getCategoryId(),
                post.getTitle(),
                post.getContent()
        ).orElseThrow();
    }

    private Post update(Post post) {
        // language=PostgreSQL
        final var query = """
            UPDATE posts SET category_id = ?, title = ?, content = ? WHERE id = ?
            RETURNING id, author_id, category_id, title, content, created
        """;
        return jdbcTemplate.single(query, postRowMapper,
                post.getCategoryId(),
                post.getTitle(),
                post.getContent(),
                post.getId()
        ).orElseThrow();
    }

}
