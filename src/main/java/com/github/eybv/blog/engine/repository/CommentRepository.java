package com.github.eybv.blog.engine.repository;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.jdbc.JdbcTemplate;
import com.github.eybv.blog.core.jdbc.RowMapper;
import com.github.eybv.blog.engine.model.Comment;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Comment> commentRowMapper = resultSet -> new Comment(
            resultSet.getLong("id"),
            resultSet.getLong("post_id"),
            resultSet.getLong("author_id"),
            resultSet.getString("content"),
            resultSet.getTimestamp("created").toInstant()
    );

    public Optional<Comment> findById(long id) {
        // language=PostgreSQL
        final var query = """
            SELECT id, post_id, author_id, content, created FROM comments
            WHERE id = ? AND deleted IS NULL
        """;
        return jdbcTemplate.single(query, commentRowMapper, id);
    }

    public List<Comment> findAllByPostId(long postId, long limit, long offset) {
        // language=PostgreSQL
        final var query = """
            SELECT id, post_id, author_id, content, created FROM comments
            WHERE post_id = ? AND deleted IS NULL
            LIMIT ? OFFSET ?
        """;
        return jdbcTemplate.query(query, commentRowMapper, postId, limit, offset);
    }

    public Comment save(Comment comment) {
        return comment.getId() == 0 ? insert(comment) : update(comment);
    }

    public void remove(Comment comment) {
        // language=PostgreSQL
        final var query = "UPDATE comments SET deleted = current_timestamp WHERE id = ?";
        jdbcTemplate.execute(query, comment.getId());
    }

    private Comment insert(Comment comment) {
        // language=PostgreSQL
        final var query = """
            INSERT INTO comments (post_id, author_id, content) VALUES (?, ?, ?)
            RETURNING id, post_id, author_id, content, created
        """;
        return jdbcTemplate.single(query, commentRowMapper,
                comment.getPostId(),
                comment.getAuthorId(),
                comment.getContent()
        ).orElseThrow();
    }

    private Comment update(Comment comment) {
        // language=PostgreSQL
        final var query = """
            UPDATE comments SET content = ? WHERE id = ?
            RETURNING id, post_id, author_id, content, created
        """;
        return jdbcTemplate.single(query, commentRowMapper,
                comment.getContent(),
                comment.getId()
        ).orElseThrow();
    }

}
