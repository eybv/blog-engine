package com.github.eybv.blog.engine.dto;

import lombok.Value;

@Value
public class CreatePostRequest {

    String title;

    String content;

    long categoryId;

}
