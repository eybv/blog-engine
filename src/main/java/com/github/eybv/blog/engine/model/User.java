package com.github.eybv.blog.engine.model;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    private long id;

    private String username;

    private boolean active;

}
