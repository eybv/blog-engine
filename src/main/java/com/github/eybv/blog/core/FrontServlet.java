package com.github.eybv.blog.core;

import com.github.eybv.blog.core.context.ContextAttribute;
import com.github.eybv.blog.core.error.HttpException;
import com.github.eybv.blog.core.error.MethodNotAllowedException;
import com.github.eybv.blog.core.error.NotFoundException;
import com.github.eybv.blog.core.handler.RequestHandler;
import com.github.eybv.blog.core.request.RequestAttribute;
import com.github.eybv.blog.core.request.RequestMatcher;
import com.github.eybv.blog.core.request.RequestMethod;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class FrontServlet extends HttpServlet {

    private Map<RequestMatcher, Map<RequestMethod, RequestHandler>> bindings;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        bindings = (Map<RequestMatcher, Map<RequestMethod, RequestHandler>>) getServletContext().getAttribute(ContextAttribute.REQUEST_BINDINGS);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final var path = request.getRequestURI().substring(request.getContextPath().length());

            final var route = bindings.entrySet().stream()
                    .filter(x -> x.getKey().matches(path))
                    .findFirst()
                    .orElseThrow(NotFoundException::new);

            request.setAttribute(RequestAttribute.PATH_VARIABLES, route.getKey().getPathVariables(path));

            final var handler = route.getValue().entrySet().stream()
                    .filter(x -> x.getKey().name().equals(request.getMethod()))
                    .findFirst()
                    .orElseThrow(MethodNotAllowedException::new)
                    .getValue();

            handler.handle(request, response);

        } catch (HttpException e) {
            response.setStatus(e.getHttpStatusCode());
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
            e.printStackTrace();
        } catch (Exception e) {
            response.sendError(500, "Internal Server Error");
            e.printStackTrace();
        }

    }

}
