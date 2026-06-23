package com.sbms.customer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum MaritalStatus {
    SINGLE,
    MARRIED,
    DIVORCED,
    WIDOWED;

    @JsonCreator
    public static MaritalStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return MaritalStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
