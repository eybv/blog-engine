package com.github.eybv.blog.core.security;

import java.util.Collection;
import java.util.List;

public class AnonymousAuthenticationToken implements Authentication {

    @Override
    public Object getPrincipal() {
        return -1;
    }

    @Override
    public Collection<String> getAuthorities() {
        return List.of("ROLE_ANONYMOUS");
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {

    }

}
