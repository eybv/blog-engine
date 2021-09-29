package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.dto.PostData;
import com.github.eybv.blog.engine.exception.ResourceNotFoundException;
import com.github.eybv.blog.engine.model.Post;
import com.github.eybv.blog.engine.repository.PostRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostService {

    private final UserService userService;
    private final CategoryService categoryService;

    private final PostRepository postRepository;

    public Optional<PostData> findById(long id) {
        return postRepository.findById(id).map(this::mapPostToDto);
    }

    public List<PostData> findAll(long limit, long offset) {
        return postRepository.findAll(limit, offset).stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    public List<PostData> findAllByCategoryId(long categoryId, long limit, long offset) {
        categoryService.findById(categoryId).orElseThrow(() -> newCategoryNotFoundException(categoryId));
        return postRepository.findAllByCategoryId(categoryId, limit, offset).stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    public List<PostData> findAllByAuthorId(long authorId, long limit, long offset) {
        userService.findById(authorId).orElseThrow(() -> newUserNotFoundException(authorId));
        return postRepository.findAllByAuthorId(authorId, limit, offset).stream()
                .map(this::mapPostToDto)
                .collect(Collectors.toList());
    }

    public PostData createPost(long authorId, long categoryId, String title, String content) {
        userService.findById(authorId).orElseThrow(() -> newUserNotFoundException(authorId));
        categoryService.findById(categoryId).orElseThrow(() -> newCategoryNotFoundException(categoryId));

        var post = new Post(0, authorId, categoryId, title, content, null);
        post = postRepository.save(post);

        return mapPostToDto(post);
    }

    public PostData updatePost(long postId, long categoryId, String title, String content) {
        categoryService.findById(categoryId).orElseThrow(() -> newCategoryNotFoundException(categoryId));

        var post = postRepository.findById(postId).orElseThrow(() -> newPostNotFoundException(postId));

        post.setCategoryId(categoryId);
        post.setTitle(title);
        post.setContent(content);

        post = postRepository.save(post);

        return mapPostToDto(post);
    }

    public void removePost(long postId) {
        var post = postRepository.findById(postId).orElseThrow(() -> newPostNotFoundException(postId));
        postRepository.remove(post);
    }

    private PostData mapPostToDto(Post post) {
        return new PostData(
                post.getId(),
                userService.findById(post.getAuthorId()).orElseThrow(),
                categoryService.findById(post.getCategoryId()).orElseThrow(),
                post.getTitle(),
                post.getContent(),
                post.getCreated().toEpochMilli()
        );
    }

    private RuntimeException newUserNotFoundException(long userId) {
        final var error = "User with ID %s not found".formatted(userId);
        return new ResourceNotFoundException(error);
    }

    private RuntimeException newCategoryNotFoundException(long categoryId) {
        final var error = "Category with ID %s not found".formatted(categoryId);
        return new ResourceNotFoundException(error);
    }

    private RuntimeException newPostNotFoundException(long postId) {
        final var error = "Post with ID %s not found".formatted(postId);
        return new ResourceNotFoundException(error);
    }

}
