package com.sbms.config;

import com.sbms.common.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class PermissionAuthorizationService {

    private static final Map<String, String> MODULE_ACCESS_BY_PREFIX = new LinkedHashMap<>();

    static {
        MODULE_ACCESS_BY_PREFIX.put("/api/roles", "ROLE_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/dashboard", "ADMIN_DASHBOARD_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/users", "USER_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/lookups", "LOOKUP_CONFIG_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/branches", "BRANCH_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/atm-terminals", "ATM_CDM_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/customers", "CUSTOMER_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/kyc", "KYC_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/account-opening-requests", "ACCOUNT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/account-types", "ACCOUNT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/accounts", "ACCOUNT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/transactions", "TRANSACTIONS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/profit", "PROFIT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/profit-ratios", "PROFIT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/profit-schedules", "PROFIT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/profit-postings", "PROFIT_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/cards", "CARD_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/customer-statements", "STATEMENTS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/branch-statements", "STATEMENTS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/statements", "STATEMENTS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/deposit-schemes", "DEPOSIT_SCHEMES_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/financing-products", "FINANCING_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/financing-applications", "FINANCING_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/financing", "FINANCING_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/contracts", "CONTRACTS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/shariah-reviews", "SHARIAH_REVIEW_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/zakat", "ZAKAT_CHARITY_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/notifications", "NOTIFICATION_ALERTS_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/integrations", "INTEGRATION_MANAGEMENT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/reports", "REPORTING_REGULATORY_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/security", "SECURITY_AUDIT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/workflows", "WORKFLOW_SUPPORT_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/verifications", "VERIFICATION_ACCESS");
        MODULE_ACCESS_BY_PREFIX.put("/api/calculations", "CALCULATION_ENGINE_ACCESS");
    }

    public void authorize(HttpServletRequest request, HandlerMethod handlerMethod, List<String> grantedPermissions) {
        Set<String> requiredPermissions = new LinkedHashSet<>();
        collectAnnotatedPermissions(requiredPermissions, handlerMethod.getBeanType().getAnnotation(RequiresPermission.class));
        collectAnnotatedPermissions(requiredPermissions, handlerMethod.getMethodAnnotation(RequiresPermission.class));

        if (requiredPermissions.isEmpty()) {
            String inferredPermission = inferModuleAccessPermission(request);
            if (inferredPermission != null) {
                requiredPermissions.add(inferredPermission);
            }
        }

        if (requiredPermissions.isEmpty()) {
            return;
        }

        Set<String> normalizedGranted = normalize(grantedPermissions);
        List<String> missing = requiredPermissions.stream()
                .filter(code -> !normalizedGranted.contains(code.toUpperCase(Locale.ROOT)))
                .toList();

        if (!missing.isEmpty()) {
            throw new ForbiddenException("You do not have permission to access this resource");
        }
    }

    private void collectAnnotatedPermissions(Set<String> requiredPermissions, RequiresPermission annotation) {
        if (annotation == null || annotation.value() == null) {
            return;
        }
        for (String value : annotation.value()) {
            if (value != null && !value.trim().isEmpty()) {
                requiredPermissions.add(value.trim().toUpperCase(Locale.ROOT));
            }
        }
    }

    private Set<String> normalize(List<String> grantedPermissions) {
        Set<String> normalized = new LinkedHashSet<>();
        if (grantedPermissions == null) {
            return normalized;
        }
        for (String permission : grantedPermissions) {
            if (permission != null && !permission.trim().isEmpty()) {
                normalized.add(permission.trim().toUpperCase(Locale.ROOT));
            }
        }
        return normalized;
    }

    private String inferModuleAccessPermission(HttpServletRequest request) {
        String path = request == null ? null : request.getRequestURI();
        if (path == null || path.isBlank()) {
            return null;
        }
        if (path.startsWith("/api/verifications/step-up")) {
            return null;
        }
        for (Map.Entry<String, String> entry : MODULE_ACCESS_BY_PREFIX.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
