package com.github.eybv.blog.core.handler;

import com.github.eybv.blog.core.error.ForbiddenException;
import com.github.eybv.blog.core.request.RequestAttribute;
import com.github.eybv.blog.core.security.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecuredRequestHandler implements RequestHandler {

    private final RequestHandler handler;

    private final List<String> authorities;

    public SecuredRequestHandler(RequestHandler handler, String... authorities) {
        this.authorities = Arrays.stream(authorities).collect(Collectors.toList());
        this.handler = handler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional.ofNullable(request.getAttribute(RequestAttribute.AUTHENTICATION))
                .map(authentication -> (Authentication) authentication)
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .filter(authorities::contains)
                .findAny()
                .orElseThrow(ForbiddenException::new);
        handler.handle(request, response);
    }

}
