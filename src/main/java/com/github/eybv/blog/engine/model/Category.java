package com.github.eybv.blog.engine.model;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {

    private long id;

    private String name;

}
