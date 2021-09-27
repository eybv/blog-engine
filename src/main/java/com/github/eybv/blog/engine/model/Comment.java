package com.github.eybv.blog.engine.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {

    private long id;

    private long postId;

    private long authorId;

    private String content;

    private LocalDateTime created;

}
