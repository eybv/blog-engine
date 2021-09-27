package com.github.eybv.blog.engine.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "value")
public class Token {

    private String value;

    private long userId;

    private LocalDateTime created;

    private LocalDateTime lastUsed;

}
