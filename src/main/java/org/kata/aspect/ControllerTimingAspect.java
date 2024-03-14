package org.kata.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

//@Aspect
//@Component
//public class ControllerTimingAspect {
//
//    private final MeterRegistry meterRegistry;
//
//    public ControllerTimingAspect(MeterRegistry meterRegistry) {
//        this.meterRegistry = meterRegistry;
//    }
//
//    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
//    public void controllerBean() {}
//
//    @Pointcut("execution(* *(..))")
//    public void methodPointcut() {}
//
//    @Around("controllerBean() && methodPointcut()")
//    public Object aroundControllerMethods(ProceedingJoinPoint pjp) throws Throwable {
//        long start = System.currentTimeMillis();
//        try {
//            return pjp.proceed();
//        } finally {
//            long duration = System.currentTimeMillis() - start;
//            Timer.builder("controller.execution.time")
//                    .description("Time taken to execute controller method")
//                    .tags("method", pjp.getSignature().getName(), "class", pjp.getTarget().getClass().getSimpleName())
//                    .register(meterRegistry)
//                    .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
//        }
//    }
//}

@Aspect
@Component
public class ControllerTimingAspect {

    private final MeterRegistry meterRegistry;

    public ControllerTimingAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Pointcut для перехвата всех методов, аннотированных аннотациями маппинга запросов
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingMethods() {}

    @Around("requestMappingMethods()")
    public Object aroundRequestMappingMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.nanoTime() - start;
            Timer.builder("http.request.time")
                    .description("Time taken to process the HTTP request")
                    .tags("method", joinPoint.getSignature().getName(),
                            "class", joinPoint.getTarget().getClass().getSimpleName())
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
        }
    }
}