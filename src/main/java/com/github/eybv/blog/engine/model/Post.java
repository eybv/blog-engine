package com.github.eybv.blog.engine.model;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Post {

    private long id;

    private long authorId;

    private long categoryId;

    private String title;

    private String content;

    private Instant created;

}
