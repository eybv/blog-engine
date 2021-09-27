package com.github.eybv.blog.core.security;

import java.util.Collection;

public interface Authentication {

    Object getPrincipal();

    Collection<String> getAuthorities();

    boolean isAuthenticated();

    void setAuthenticated(boolean authenticated);

}
