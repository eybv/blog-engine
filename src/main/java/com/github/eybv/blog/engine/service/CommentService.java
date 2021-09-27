package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.dto.CommentData;
import com.github.eybv.blog.engine.exception.ResourceNotFoundException;
import com.github.eybv.blog.engine.model.Comment;
import com.github.eybv.blog.engine.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final UserService userService;
    private final PostService postService;

    private final CommentRepository commentRepository;

    public Optional<CommentData> findById(long id) {
        return commentRepository.findById(id).map(this::mapCommentToDto);
    }

    public List<CommentData> findAllByPostId(long postId, long limit, long offset) {
        postService.findById(postId).orElseThrow(() -> newPostNotFoundException(postId));
        return commentRepository.findAllByPostId(postId, limit, offset).stream()
                .map(this::mapCommentToDto)
                .collect(Collectors.toList());
    }

    public CommentData createComment(long authorId, long postId, String content) {
        userService.findById(authorId).orElseThrow(() -> newUserNotFoundException(authorId));
        postService.findById(postId).orElseThrow(() -> newPostNotFoundException(postId));

        var comment = new Comment(0, postId, authorId, content, null);
        comment = commentRepository.save(comment);

        return mapCommentToDto(comment);
    }

    public CommentData updateComment(long commentId, String content) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> newCommentNotFoundException(commentId));
        comment.setContent(content);
        comment = commentRepository.save(comment);
        return mapCommentToDto(comment);
    }

    public void removeComment(long commentId) {
        final var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> newCommentNotFoundException(commentId));
        commentRepository.remove(comment);
    }

    private CommentData mapCommentToDto(Comment comment) {
        return new CommentData(
                comment.getId(),
                comment.getPostId(),
                userService.findById(comment.getAuthorId()).orElseThrow(),
                comment.getContent(),
                Timestamp.valueOf(comment.getCreated()).getTime()
        );
    }

    private RuntimeException newPostNotFoundException(long postId) {
        final var error = "Post with ID %s not found".formatted(postId);
        return new ResourceNotFoundException(error);
    }

    private RuntimeException newUserNotFoundException(long userId) {
        final var error = "User with ID %s not found".formatted(userId);
        return new ResourceNotFoundException(error);
    }

    private RuntimeException newCommentNotFoundException(long commentId) {
        final var error = "Comment with ID %s not found".formatted(commentId);
        return new ResourceNotFoundException(error);
    }

}
