package com.github.eybv.blog.engine.dto;

import lombok.Value;

@Value
public class PostData {

    long id;

    UserData author;

    CategoryData category;

    String title;

    String content;

    long created;

}
