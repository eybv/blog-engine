package com.github.eybv.blog.engine.security;

import com.github.eybv.blog.core.security.Authentication;

import java.util.Collection;

public class SimpleAuthenticationToken implements Authentication {

    private final Object principal;

    private final Collection<String> authorities;

    private boolean authenticated = false;


    public SimpleAuthenticationToken(Object principal, Collection<String> authorities) {
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Collection<String> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

}
