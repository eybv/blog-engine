package com.github.eybv.blog.engine.dto;

import lombok.Value;

@Value
public class CommentData {

    long id;

    long postId;

    UserData author;

    String content;

    long created;

}
