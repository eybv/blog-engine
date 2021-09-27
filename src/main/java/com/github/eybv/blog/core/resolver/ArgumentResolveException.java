package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.handler.HandlerException;

public class ArgumentResolveException extends ArgumentResolverException {

    public ArgumentResolveException() {
        super();
    }

    public ArgumentResolveException(String message) {
        super(message);
    }

    public ArgumentResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentResolveException(Throwable cause) {
        super(cause);
    }

}
