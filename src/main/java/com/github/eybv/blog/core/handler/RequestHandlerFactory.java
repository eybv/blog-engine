package com.github.eybv.blog.core.handler;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestMapping;
import com.github.eybv.blog.core.converter.TextMediaTypeConverterFactory;
import com.github.eybv.blog.core.error.HttpException;
import com.github.eybv.blog.core.resolver.ArgumentResolver;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestHandlerFactory {

    private final List<ArgumentResolver> resolvers;

    private final TextMediaTypeConverterFactory converterFactory;

    public RequestHandler fromAnnotatedMethod(Method method, Object instance) {
        return (request, response) -> {
            final var invocationArgs = new ArrayList<>();
            for (Parameter parameter : method.getParameters()) {
                var resolved = false;
                for (ArgumentResolver resolver : resolvers) {
                    if (!resolver.canResolve(parameter)) continue;
                    invocationArgs.add(resolver.resolve(parameter, request, response));
                    resolved = true;
                    break;
                }
                if (resolved) continue;
                final var error = "Cannot resolve parameter %s";
                throw new HandlerInvocationException(String.format(error, parameter.getType().getName()));
            }

            final var result = invokeSneakyThrows(method, instance, invocationArgs);
            final var requestMapping = method.getAnnotation(RequestMapping.class);
            final var converter = converterFactory.getConverter(requestMapping.produce());

            if (result == null) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setContentType(requestMapping.produce().getType());
                response.getWriter().write(converter.convert(result));
            }
        };
    }

    private Object invokeSneakyThrows(Method method, Object instance, List<?> args) {
        try {
            return method.invoke(instance, args.toArray());
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof HttpException) {
                throw (HttpException) e.getCause();
            }
            throw new HandlerInvocationException(e);
        }
    }

}
