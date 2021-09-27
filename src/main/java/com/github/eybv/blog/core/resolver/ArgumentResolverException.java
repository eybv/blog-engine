package com.github.eybv.blog.core.resolver;

public class ArgumentResolverException extends RuntimeException {

    public ArgumentResolverException() {
        super();
    }

    public ArgumentResolverException(String message) {
        super(message);
    }

    public ArgumentResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentResolverException(Throwable cause) {
        super(cause);
    }

}
