package com.github.eybv.blog.engine.model;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "user")
public class UserWithPassword {

    private User user;

    private String password;

}
