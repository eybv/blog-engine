package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.request.RequestAttribute;
import com.github.eybv.blog.core.security.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;

@Component
public class AuthenticationArgumentResolver implements ArgumentResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return Authentication.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        return request.getAttribute(RequestAttribute.AUTHENTICATION);
    }

}
