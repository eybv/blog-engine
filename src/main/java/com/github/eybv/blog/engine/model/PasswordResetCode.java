package com.github.eybv.blog.engine.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PasswordResetCode {

    private long id;

    private UUID value;

    private long userId;

    private LocalDateTime created;

}
