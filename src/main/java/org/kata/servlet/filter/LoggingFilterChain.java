package org.kata.servlet.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoggingFilterChain implements FilterChain {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("Request URI: {}, Method: {}", request.getRequestURI(), request.getMethod());
        log.info("Response Status: {}, Content Type: {}", response.getStatus(), response.getContentType());
    }
}
