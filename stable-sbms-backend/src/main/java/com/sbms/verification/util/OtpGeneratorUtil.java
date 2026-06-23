package com.sbms.verification.util;

import java.security.SecureRandom;

public final class OtpGeneratorUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpGeneratorUtil() {
    }

    public static String generateSixDigitCode() {
        int value = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(value);
    }
}
