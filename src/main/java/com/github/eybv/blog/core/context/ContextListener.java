package com.github.eybv.blog.core.context;

import com.github.eybv.blog.core.annotation.HasRole;
import com.github.eybv.blog.core.annotation.Order;
import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.core.annotation.RequestMapping;
import com.github.eybv.blog.core.handler.RequestHandler;
import com.github.eybv.blog.core.handler.RequestHandlerFactory;
import com.github.eybv.blog.core.handler.SecuredRequestHandler;
import com.github.eybv.blog.core.request.RequestMatcher;
import com.github.eybv.blog.core.request.RequestMethod;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

@Log
public class ContextListener implements ServletContextListener {

    private final HashMap<Class<?>, Object> beans = new HashMap<>();
    private final Set<Class<?>> beanDefinitions = new HashSet<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        try {
            final var context = sce.getServletContext();
            final var initialContext = new InitialContext();

            beans.put(DataSource.class, initialContext.lookup("java:/comp/env/jdbc/db"));

            final var config = new ConfigurationBuilder()
                    .addUrls(ClasspathHelper.forPackage("com.github.eybv"))
                    .addScanners(new TypeAnnotationsScanner())
                    .addScanners(new MethodAnnotationsScanner());
            final var reflections = new Reflections(config);

            beanDefinitions.addAll(reflections.getTypesAnnotatedWith(Component.class));
            beanDefinitions.forEach(this::instantiateBeanRecursively);

            getSortedBeansWithTypeOf(Filter.class).forEach(x -> {
                log.info("Register filter: %s".formatted(x.getClass().getName()));
                final var filterRegistration = context.addFilter(x.getClass().getName(), (Filter) x);
                filterRegistration.addMappingForUrlPatterns(null, true, "/*");
            });

            final var handlerFactory = (RequestHandlerFactory) getBeanWithTypeOf(RequestHandlerFactory.class);
            final var delegates = reflections.getMethodsAnnotatedWith(RequestMapping.class);

            final var bindings = new HashMap<RequestMatcher, Map<RequestMethod, RequestHandler>>();

            for (Method delegate : delegates) {
                final var requestMapping = delegate.getAnnotation(RequestMapping.class);
                final var requestMatcher = new RequestMatcher(requestMapping.path());

                log.info("Found handler: %s %s at %s".formatted(
                        requestMapping.method().name(),
                        requestMapping.path(),
                        delegate.getDeclaringClass().getName()));

                Optional.ofNullable(bindings.get(requestMatcher))
                        .map(methods -> methods.get(requestMapping.method()))
                        .map(x -> new Object[] { requestMapping.method().name(), requestMapping.path() })
                        .map(x -> String.format("Handler %s %s already exists", x))
                        .ifPresent(x -> { throw new DuplicateBindingsException(x); });

                final var instance = getBeanWithTypeOf(delegate.getDeclaringClass());
                var handler = handlerFactory.fromAnnotatedMethod(delegate, instance);

                if (delegate.isAnnotationPresent(HasRole.class)) {
                    final var authorities = delegate.getAnnotation(HasRole.class).value();
                    handler = new SecuredRequestHandler(handler, authorities);
                }

                final var methods = bindings.getOrDefault(requestMatcher, new HashMap<>());
                methods.put(requestMapping.method(), handler);
                bindings.put(requestMatcher, methods);
            }

            context.setAttribute(ContextAttribute.REQUEST_BINDINGS, bindings);

        } catch (Exception e) {
            throw new ContextInitializationException(e);
        }
    }

    /**
     * NB: A circular dependency throws a StackOverflowException.
     */
    @SneakyThrows
    private void instantiateBeanRecursively(Class<?> clazz) {
        if (beans.containsKey(clazz)) return;
        final var constructor = clazz.getDeclaredConstructors()[0];
        final var constrictorArgs = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            final var type = parameter.getType();
            if (Collection.class.isAssignableFrom(type)) {
                final var parametrizedType = (ParameterizedType) parameter.getParameterizedType();
                final var parameterTypeName = parametrizedType.getActualTypeArguments()[0].getTypeName();
                final var parameterType = Class.forName(parameterTypeName);
                instantiateBeanCollectionRecursively(parameterType);
                constrictorArgs.add(getBeansWithTypeOf(parameterType));
                continue;
            }
            if (!containsBeansWithTypeOf(type) && containsBeanDefinitionsWithTypeOf(type)) {
                instantiateBeanRecursively(getBeanDefinitionWithTypeOf(type));
            }
            if (containsBeansWithTypeOf(type)) {
                constrictorArgs.add(getBeanWithTypeOf(type));
                continue;
            }
            final var error = "Cannot resolve dependency %s";
            throw new ContextInitializationException(String.format(error, type.getName()));
        }
        beans.put(clazz, constructor.newInstance(constrictorArgs.toArray()));
    }

    private void instantiateBeanCollectionRecursively(Class<?> clazz) {
        getBeanDefinitionsWithTypeOf(clazz).forEach(this::instantiateBeanRecursively);
    }

    private boolean containsBeansWithTypeOf(Class<?> type) {
        return beans.keySet().stream().anyMatch(type::isAssignableFrom);
    }

    private boolean containsBeanDefinitionsWithTypeOf(Class<?> type) {
        return beanDefinitions.stream().anyMatch(type::isAssignableFrom);
    }

    private Object getBeanWithTypeOf(Class<?> type) {
        return beans.entrySet().stream()
                .filter(x -> type.isAssignableFrom(x.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    private Collection<Object> getBeansWithTypeOf(Class<?> type) {
        return beans.entrySet().stream()
                .filter(x -> type.isAssignableFrom(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private Collection<Object> getSortedBeansWithTypeOf(Class<?> type) {
        return getBeansWithTypeOf(type).stream()
                .sorted(Comparator.comparingInt(this::getBeanOrder))
                .collect(Collectors.toList());
    }

    private int getBeanOrder(Object bean) {
        if (bean.getClass().isAnnotationPresent(Order.class)) {
            return bean.getClass().getAnnotation(Order.class).value();
        }
        return Integer.MAX_VALUE;
    }

    private Class<?> getBeanDefinitionWithTypeOf(Class<?> type) {
        return beanDefinitions.stream()
                .filter(type::isAssignableFrom)
                .findFirst()
                .orElseThrow();
    }

    private Collection<Class<?>> getBeanDefinitionsWithTypeOf(Class<?> type) {
        return beanDefinitions.stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toList());
    }

}
