package com.bwgjoseph.springbootcsstack.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogExecutionTimeAspect {
    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();

        Object object = pjp.proceed();
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        watch.stop();

        log.info("Time taken is for method {} took {} seconds", signature.toShortString(), watch.getTotalTimeSeconds());

        return object;
    }
}