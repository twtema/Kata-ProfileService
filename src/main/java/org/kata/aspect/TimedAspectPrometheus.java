package org.kata.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;


@Aspect
@Component
public class TimedAspectPrometheus {
    private static final Logger log = LoggerFactory.getLogger(TimedAspectPrometheus.class);

    @Around("@annotation(io.micrometer.core.annotation.Timed)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log.info(joinPoint.getSignature() + " выполнен за " + executionTime + "мс");
        return proceed;
    }
}