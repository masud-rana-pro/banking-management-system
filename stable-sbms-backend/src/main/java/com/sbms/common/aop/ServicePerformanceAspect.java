package com.sbms.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServicePerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(ServicePerformanceAspect.class);
    private static final long WARN_THRESHOLD_MS = 1200L;
    private static final long INFO_THRESHOLD_MS = 500L;

    @Around("execution(public * com.sbms..service..*(..)) && !within(com.sbms.common.aop..*)")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            logIfSlow(joinPoint, System.currentTimeMillis() - startedAt, false, null);
            return result;
        } catch (Throwable ex) {
            logIfSlow(joinPoint, System.currentTimeMillis() - startedAt, true, ex);
            throw ex;
        }
    }

    private void logIfSlow(ProceedingJoinPoint joinPoint, long durationMs, boolean failed, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        String username = AopRequestContext.currentUsername();
        if (failed || durationMs >= WARN_THRESHOLD_MS) {
            log.warn("AOP service {} {} in {} ms user={} reason={}",
                    method,
                    failed ? "failed" : "completed",
                    durationMs,
                    username,
                    ex == null ? "slow-call" : ex.getClass().getSimpleName());
            return;
        }
        if (durationMs >= INFO_THRESHOLD_MS) {
            log.info("AOP service {} completed in {} ms user={}", method, durationMs, username);
        }
    }
}
