package com.sbms.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebRequestTracingAspect {

    private static final Logger log = LoggerFactory.getLogger(WebRequestTracingAspect.class);

    @Around("execution(public * com.sbms..controller..*(..)) && !within(com.sbms.common.aop..*)")
    public Object traceControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = AopRequestContext.currentRequest();
        long startedAt = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            logCompletion(joinPoint, request, System.currentTimeMillis() - startedAt, false, null);
            return result;
        } catch (Throwable ex) {
            logCompletion(joinPoint, request, System.currentTimeMillis() - startedAt, true, ex);
            throw ex;
        }
    }

    private void logCompletion(ProceedingJoinPoint joinPoint,
                               HttpServletRequest request,
                               long durationMs,
                               boolean failed,
                               Throwable ex) {
        HttpServletResponse response = AopRequestContext.currentResponse();
        String method = request == null ? "N/A" : request.getMethod();
        String uri = request == null ? joinPoint.getSignature().toShortString() : request.getRequestURI();
        String username = AopRequestContext.currentUsername();
        int status = response == null ? (failed ? 500 : 200) : response.getStatus();

        if (failed) {
            log.error("AOP request {} {} failed status={} duration={}ms user={} error={}",
                    method, uri, status, durationMs, username,
                    ex == null ? "unknown" : ex.getClass().getSimpleName());
            return;
        }

        if (durationMs >= 800 || !"GET".equalsIgnoreCase(method)) {
            log.info("AOP request {} {} status={} duration={}ms user={}",
                    method, uri, status, durationMs, username);
        } else {
            log.debug("AOP request {} {} status={} duration={}ms user={}",
                    method, uri, status, durationMs, username);
        }
    }
}
