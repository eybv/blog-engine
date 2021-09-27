package com.github.eybv.blog.engine.exception;

import com.github.eybv.blog.core.error.BadRequestException;

public class ResourceAlreadyExists extends BadRequestException {

    public ResourceAlreadyExists() {
        super();
    }

    public ResourceAlreadyExists(String message) {
        super(message);
    }

    public ResourceAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

}
