package com.github.eybv.blog.engine.dto;

import lombok.Value;

@Value
public class UpdatePostRequest {

    long id;

    String title;

    String content;

    long categoryId;

}
