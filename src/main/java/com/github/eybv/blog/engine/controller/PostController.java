package com.github.eybv.blog.engine.controller;

import com.github.eybv.blog.core.annotation.*;
import com.github.eybv.blog.core.error.NotFoundException;
import com.github.eybv.blog.core.request.RequestMethod;
import com.github.eybv.blog.core.security.Authentication;
import com.github.eybv.blog.engine.dto.CreatePostRequest;
import com.github.eybv.blog.engine.dto.PostData;
import com.github.eybv.blog.engine.dto.UpdatePostRequest;
import com.github.eybv.blog.engine.exception.AuthenticationException;
import com.github.eybv.blog.engine.service.PostService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostController {

    private static final long DEFAULT_FETCH_LIMIT = 50;

    private final PostService postService;

    @RequestMapping(method = RequestMethod.GET, path = "/post/{id}")
    public PostData getById(@PathVariable("id") Long postId) {
        final var error = "Post with ID %s not found".formatted(postId);
        return postService.findById(postId)
                .orElseThrow(() -> new NotFoundException(error));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/post")
    public List<PostData> getPostList(
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "offset", required = false) Long offset
    ) {
        limit = Optional.ofNullable(limit).orElse(DEFAULT_FETCH_LIMIT);
        offset = Optional.ofNullable(offset).orElse(0L);
        return postService.findAll(limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/post/category/{id}")
    public List<PostData> getPostListByCategoryId(
            @PathVariable("id") Long categoryId,
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "offset", required = false) Long offset
    ) {
        limit = Optional.ofNullable(limit).orElse(DEFAULT_FETCH_LIMIT);
        offset = Optional.ofNullable(offset).orElse(0L);
        return postService.findAllByCategoryId(categoryId, limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/post/author/{id}")
    public List<PostData> getPostListByAuthorId(
            @PathVariable("id") Long authorId,
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "offset", required = false) Long offset
    ) {
        limit = Optional.ofNullable(limit).orElse(DEFAULT_FETCH_LIMIT);
        offset = Optional.ofNullable(offset).orElse(0L);
        return postService.findAllByAuthorId(authorId, limit, offset);
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.POST, path = "/post")
    public PostData createPost(@RequestBody CreatePostRequest request, Authentication authentication) {
        final var userId = (long) authentication.getPrincipal();
        return postService.createPost(userId, request.getCategoryId(), request.getTitle(), request.getContent());
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.PATCH, path = "/post")
    public PostData updatePost(@RequestBody UpdatePostRequest request, Authentication authentication) {
        final var userId = (long) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains("ROLE_ADMIN")) {
            postService.findById(request.getId())
                    .filter(post -> post.getAuthor().getId() == userId)
                    .orElseThrow(AuthenticationException::new);
        }
        return postService.updatePost(request.getId(), request.getCategoryId(), request.getTitle(), request.getContent());
    }

    @HasRole("ROLE_USER")
    @RequestMapping(method = RequestMethod.DELETE, path = "/post/{id}")
    public void removePost(@PathVariable("id") Long postId, Authentication authentication) {
        final var userId = (long) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains("ROLE_ADMIN")) {
            postService.findById(postId)
                    .filter(post -> post.getAuthor().getId() == userId)
                    .orElseThrow(AuthenticationException::new);
        }
        postService.removePost(postId);
    }

}
