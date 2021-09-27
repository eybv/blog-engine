package com.github.eybv.blog.engine.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserData {

    long id;

    String username;

    List<String> roles;

    boolean active;

}
