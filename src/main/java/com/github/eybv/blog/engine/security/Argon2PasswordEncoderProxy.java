package com.github.eybv.blog.engine.security;

import com.github.eybv.blog.core.annotation.Component;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Component
public class Argon2PasswordEncoderProxy extends Argon2PasswordEncoder {

}
