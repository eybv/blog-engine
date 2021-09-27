package com.github.eybv.blog.engine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ROLE_ADMIN(1),
    ROLE_USER(2);

    private final long id;

}
