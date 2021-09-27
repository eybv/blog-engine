package com.github.eybv.blog.core.security;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.Order;

import com.github.eybv.blog.core.request.RequestAttribute;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Order(Integer.MAX_VALUE)
@Component
public class AnonymousAuthenticationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final var existingAuthentication = (Authentication) req.getAttribute(RequestAttribute.AUTHENTICATION);
        if (existingAuthentication == null || !existingAuthentication.isAuthenticated()) {
            req.setAttribute(RequestAttribute.AUTHENTICATION, new AnonymousAuthenticationToken());
        }
        super.doFilter(req, res, chain);
    }

}
