package com.github.eybv.blog.core.resolver;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.PathVariable;
import com.github.eybv.blog.core.request.RequestAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Parameter;
import java.util.Map;

@Component
public class PathVariableArgumentResolver implements ArgumentResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class) && (isInteger(parameter) || isString(parameter));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolve(Parameter parameter, HttpServletRequest request, HttpServletResponse response) {
        if (!canResolve(parameter)) {
            var info = new Object[] { parameter.getName(), parameter.getType().getName() };
            throw new UnsupportedParameterException(String.format("%s [%s]", info));
        }

        final var annotation = parameter.getAnnotation(PathVariable.class);
        final var variables = (Map<String, String>) request.getAttribute(RequestAttribute.PATH_VARIABLES);

        if (variables == null || !variables.containsKey(annotation.value())) {
            final var error = String.format("Path variable %s not present", annotation.value());
            throw new ArgumentResolveException(error);
        }

        final var value = variables.get(annotation.value());

        return convert(parameter, value);
    }

    private Object convert(Parameter parameter, String value) {
        if (Integer.class.isAssignableFrom(parameter.getType())) {
            return Integer.valueOf(value);
        }
        if (Long.class.isAssignableFrom(parameter.getType())) {
            return Long.valueOf(value);
        }
        return value;
    }

    private boolean isString(Parameter parameter) {
        return parameter.getType().isAssignableFrom(String.class);
    }

    private boolean isInteger(Parameter parameter) {
        return Integer.class.isAssignableFrom(parameter.getType()) ||
                Long.class.isAssignableFrom(parameter.getType());
    }

}
