package com.sbms.verification.util;

public final class MaskingUtil {

    private MaskingUtil() {
    }

    public static String mask(String value, boolean email) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        if (email) {
            int at = trimmed.indexOf('@');
            if (at <= 1) {
                return "***" + trimmed.substring(Math.max(0, at));
            }
            return trimmed.substring(0, 1) + "***" + trimmed.substring(at - 1);
        }
        if (trimmed.length() <= 4) {
            return "***" + trimmed;
        }
        return trimmed.substring(0, 3) + "****" + trimmed.substring(trimmed.length() - 3);
    }
}
