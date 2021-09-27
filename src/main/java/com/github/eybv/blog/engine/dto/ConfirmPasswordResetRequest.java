package com.github.eybv.blog.engine.dto;

import lombok.Value;

@Value
public class ConfirmPasswordResetRequest {

    String username;

    String code;

    String password;

}
