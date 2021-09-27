package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.dto.CategoryData;
import com.github.eybv.blog.engine.exception.DataConsistencyException;
import com.github.eybv.blog.engine.exception.ResourceAlreadyExists;
import com.github.eybv.blog.engine.exception.ResourceNotFoundException;
import com.github.eybv.blog.engine.model.Category;
import com.github.eybv.blog.engine.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Optional<CategoryData> findById(long id) {
        return categoryRepository.findById(id)
                .map(category -> new CategoryData(category.getId(), category.getName()));
    }

    public List<CategoryData> findAll(long limit, long offset) {
        return categoryRepository.findAll(limit, offset).stream()
                .map(category -> new CategoryData(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    public CategoryData createCategory(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyExists("Category %s already exists".formatted(name));
        }
        final var category = categoryRepository.save(new Category(0, name.trim()));
        return new CategoryData(category.getId(), category.getName());
    }

    public CategoryData updateCategory(long categoryId, String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyExists("Category %s already exists".formatted(name));
        }

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> newCategoryNotFoundException(categoryId));

        category.setName(name);
        category = categoryRepository.save(category);

        return new CategoryData(category.getId(), category.getName());
    }

    public void removeCategory(long categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> newCategoryNotFoundException(categoryId));
        try {
            categoryRepository.remove(category);
        } catch (Exception e) {
            final var error = "Category with id %s is associated with posts".formatted(categoryId);
            throw new DataConsistencyException(error);
        }
    }

    private RuntimeException newCategoryNotFoundException(long categoryId) {
        final var error = "Category with ID %s not found".formatted(categoryId);
        return new ResourceNotFoundException(error);
    }

}
