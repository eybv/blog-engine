package com.github.eybv.blog.engine.controller;

import com.github.eybv.blog.core.annotation.*;
import com.github.eybv.blog.core.request.RequestMethod;
import com.github.eybv.blog.engine.dto.CategoryData;
import com.github.eybv.blog.engine.dto.CreateCategoryRequest;
import com.github.eybv.blog.engine.dto.UpdateCategoryRequest;
import com.github.eybv.blog.engine.service.CategoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryController {

    private static final long DEFAULT_FETCH_LIMIT = 50;

    private final CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/category")
    public List<CategoryData> getCategoryList(
            @RequestParam(value = "limit", required = false) Long limit,
            @RequestParam(value = "offset", required = false) Long offset
    ) {
        limit = Optional.ofNullable(limit).orElse(DEFAULT_FETCH_LIMIT);
        offset = Optional.ofNullable(offset).orElse(0L);
        return categoryService.findAll(limit, offset);
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST, path = "/category")
    public CategoryData createCategory(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request.getName());
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.PATCH, path = "/category")
    public CategoryData updateCategory(@RequestBody UpdateCategoryRequest request) {
        return categoryService.updateCategory(request.getId(), request.getName());
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.DELETE, path = "/category/{id}")
    public void removeCategory(@PathVariable("id") Long categoryId) {
        categoryService.removeCategory(categoryId);
    }

}
