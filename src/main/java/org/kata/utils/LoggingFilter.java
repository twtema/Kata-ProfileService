package org.kata.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request Hash-{}, Method: {}, URI: {}, Headers: {}",
                request.hashCode(), request.getMethod(), request.getRequestURI(), getHeaders(request));
        filterChain.doFilter(request, response);
        log.info("Request hash: {}, Response Status: {}, Content Type: {}, Headers: {}",
                request.hashCode(), response.getStatus(), response.getContentType(), getHeaders(response));
    }

    private String getHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + " - " + request.getHeader(headerName))
                .collect(Collectors.joining(", "));
    }

    private String getHeaders(HttpServletResponse response) {
        return response.getHeaderNames().stream()
                .map(headerName -> headerName + " - " + response.getHeader(headerName))
                .collect(Collectors.joining(", "));
    }
}
