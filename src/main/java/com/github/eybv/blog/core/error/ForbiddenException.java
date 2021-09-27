package com.github.eybv.blog.core.error;

public class ForbiddenException extends HttpException {

    public ForbiddenException() {
        super(403, "Forbidden", "");
    }

    public ForbiddenException(String message) {
        super(403, "Forbidden", message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(403, "Forbidden", message, cause);
    }

}
