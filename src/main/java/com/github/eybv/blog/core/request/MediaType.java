package com.github.eybv.blog.core.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaType {

    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json");

    private final String type;

}
