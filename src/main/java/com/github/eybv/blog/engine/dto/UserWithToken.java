package com.github.eybv.blog.engine.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserWithToken {

    long id;

    String username;

    List<String> roles;

    String token;

}
