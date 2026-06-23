package com.sbms.common.aop;

import com.sbms.config.RequiresPermission;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Aspect
@Component
public class ControllerAuditAspect {

    private static final Map<String, String> MODULE_BY_PREFIX = new LinkedHashMap<>();

    static {
        MODULE_BY_PREFIX.put("/api/roles", "ROLE_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/users", "USER_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/lookups", "LOOKUP_CONFIG");
        MODULE_BY_PREFIX.put("/api/branches", "BRANCH_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/atm-terminals", "ATM_CDM");
        MODULE_BY_PREFIX.put("/api/customers", "CUSTOMER_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/kyc", "KYC_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/account-opening-requests", "ACCOUNT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/account-types", "ACCOUNT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/accounts", "ACCOUNT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/transactions", "TRANSACTIONS");
        MODULE_BY_PREFIX.put("/api/profit", "PROFIT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/profit-ratios", "PROFIT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/profit-schedules", "PROFIT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/profit-postings", "PROFIT_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/cards", "CARD_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/customer-statements", "STATEMENTS");
        MODULE_BY_PREFIX.put("/api/branch-statements", "STATEMENTS");
        MODULE_BY_PREFIX.put("/api/statements", "STATEMENTS");
        MODULE_BY_PREFIX.put("/api/deposit-schemes", "DEPOSIT_SCHEMES");
        MODULE_BY_PREFIX.put("/api/financing-products", "FINANCING");
        MODULE_BY_PREFIX.put("/api/financing-applications", "FINANCING");
        MODULE_BY_PREFIX.put("/api/financing", "FINANCING");
        MODULE_BY_PREFIX.put("/api/contracts", "CONTRACTS");
        MODULE_BY_PREFIX.put("/api/shariah-reviews", "SHARIAH_REVIEW");
        MODULE_BY_PREFIX.put("/api/zakat", "ZAKAT_CHARITY");
        MODULE_BY_PREFIX.put("/api/notifications", "NOTIFICATION_ALERTS");
        MODULE_BY_PREFIX.put("/api/integrations", "INTEGRATION_MANAGEMENT");
        MODULE_BY_PREFIX.put("/api/reports", "REPORTING_REGULATORY");
        MODULE_BY_PREFIX.put("/api/security", "SECURITY_AUDIT");
        MODULE_BY_PREFIX.put("/api/workflows", "WORKFLOW_SUPPORT");
        MODULE_BY_PREFIX.put("/api/verifications", "VERIFICATION");
        MODULE_BY_PREFIX.put("/api/calculations", "CALCULATION_ENGINE");
        MODULE_BY_PREFIX.put("/api/auth", "AUTH");
    }

    private final AopAuditTrailService auditTrailService;
    private final AopPayloadSanitizer payloadSanitizer;

    public ControllerAuditAspect(AopAuditTrailService auditTrailService,
                                 AopPayloadSanitizer payloadSanitizer) {
        this.auditTrailService = auditTrailService;
        this.payloadSanitizer = payloadSanitizer;
    }

    @Around("execution(public * com.sbms..controller..*(..))")
    public Object auditControllerMutation(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = AopRequestContext.currentRequest();
        if (!isAuditable(request)) {
            return joinPoint.proceed();
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String moduleName = resolveModuleName(request, joinPoint);
        String actionName = resolveActionName(method, joinPoint);
        Long referenceId = resolveReferenceId();
        String payload = payloadSanitizer.sanitizeArguments(joinPoint.getArgs());
        String description = request.getMethod() + " " + request.getRequestURI();

        try {
            Object result = joinPoint.proceed();
            auditTrailService.record(
                    moduleName,
                    referenceId,
                    actionName,
                    description,
                    payload,
                    "SUCCESS",
                    AopRequestContext.currentUserId(),
                    AopRequestContext.currentUsername(),
                    AopRequestContext.clientIp()
            );
            return result;
        } catch (Throwable ex) {
            auditTrailService.record(
                    moduleName,
                    referenceId,
                    actionName,
                    description,
                    payloadSanitizer.sanitizeSingle(Map.of(
                            "request", payload,
                            "error", ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()
                    )),
                    "FAILED",
                    AopRequestContext.currentUserId(),
                    AopRequestContext.currentUsername(),
                    AopRequestContext.clientIp()
            );
            throw ex;
        }
    }

    private boolean isAuditable(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String method = request.getMethod();
        if (!("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method))) {
            return false;
        }
        String path = request.getRequestURI();
        if (path == null || path.isBlank()) {
            return false;
        }
        return !path.startsWith("/api/auth/login")
                && !path.startsWith("/api/auth/verify-login-otp")
                && !path.startsWith("/api/auth/resend-login-otp")
                && !path.startsWith("/api/verifications/step-up")
                && !path.startsWith("/api/security/investigation-cases/");
    }

    private String resolveModuleName(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        String path = request == null ? null : request.getRequestURI();
        if (path != null) {
            for (Map.Entry<String, String> entry : MODULE_BY_PREFIX.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        String simpleName = joinPoint.getTarget().getClass().getSimpleName().replace("Controller", "");
        return simpleName.isBlank() ? "SYSTEM" : simpleName.toUpperCase(Locale.ROOT);
    }

    private String resolveActionName(Method method, ProceedingJoinPoint joinPoint) {
        RequiresPermission methodPermission = method.getAnnotation(RequiresPermission.class);
        String fromMethod = firstMeaningfulPermission(methodPermission);
        if (fromMethod != null) {
            return fromMethod;
        }
        RequiresPermission typePermission = joinPoint.getTarget().getClass().getAnnotation(RequiresPermission.class);
        String fromType = firstMeaningfulPermission(typePermission);
        if (fromType != null) {
            return fromType;
        }
        return method.getName().toUpperCase(Locale.ROOT);
    }

    private String firstMeaningfulPermission(RequiresPermission annotation) {
        if (annotation == null || annotation.value() == null) {
            return null;
        }
        for (String value : annotation.value()) {
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            String normalized = value.trim().toUpperCase(Locale.ROOT);
            if (!normalized.endsWith("_ACCESS")) {
                return normalized;
            }
        }
        return null;
    }

    private Long resolveReferenceId() {
        for (String value : AopRequestContext.pathVariables().values()) {
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                return Long.valueOf(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
