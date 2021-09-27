package com.github.eybv.blog.engine.controller;

import com.github.eybv.blog.core.annotation.*;
import com.github.eybv.blog.core.request.RequestMethod;
import com.github.eybv.blog.core.security.Authentication;
import com.github.eybv.blog.engine.dto.CommentData;
import com.github.eybv.blog.engine.dto.CreateCommentRequest;
import com.github.eybv.blog.engine.dto.UpdateCommentRequest;
import com.github.eybv.blog.engine.exception.AuthenticationException;
import com.github.eybv.blog.engine.service.CommentService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentController {

    private static final long DEFAULT_FETCH_LIMIT = 50;

    private final CommentService commentService;

    @RequestMapping(method = RequestMethod.GET, path = "/post/{id}/comment")
    public List<CommentData> getCommentListByPostId(
            @PathVariable("id") Long postId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset
    ) {
        final var limit_ = Optional.ofNullable(limit).map(Long::valueOf).orElse(DEFAULT_FETCH_LIMIT);
        final var offset_ = Optional.ofNullable(offset).map(Long::valueOf).orElse(0L);
        return commentService.findAllByPostId(postId, limit_, offset_);
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.POST, path = "/post/{id}/comment")
    public CommentData createComment(
            @PathVariable("id") Long postId,
            @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {
        final var userId = (long) authentication.getPrincipal();
        return commentService.createComment(userId, postId, request.getContent());
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.PATCH, path = "/comment/{id}")
    public CommentData updateComment(
            @PathVariable("id") Long commentId,
            @RequestBody UpdateCommentRequest request,
            Authentication authentication
    ) {
        final var userId = (long) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains("ROLE_ADMIN")) {
            commentService.findById(commentId)
                    .filter(comment -> comment.getAuthor().getId() == userId)
                    .orElseThrow(AuthenticationException::new);
        }
        return commentService.updateComment(commentId, request.getContent());
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.DELETE, path = "/comment/{id}")
    public void removeComment(@PathVariable("id") Long commentId, Authentication authentication) {
        final var userId = (long) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains("ROLE_ADMIN")) {
            commentService.findById(commentId)
                    .filter(comment -> comment.getAuthor().getId() == userId)
                    .orElseThrow(AuthenticationException::new);
        }
        commentService.removeComment(commentId);
    }

}
