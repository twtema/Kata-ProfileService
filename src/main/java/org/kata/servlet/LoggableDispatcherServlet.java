package org.kata.servlet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class LoggableDispatcherServlet extends DispatcherServlet {

    private final FilterChain filterChain;

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.doDispatch(request, response);
        filterChain.doFilter(request, response);
    }
}