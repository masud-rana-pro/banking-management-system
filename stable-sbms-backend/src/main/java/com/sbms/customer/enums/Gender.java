package com.sbms.customer.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Gender.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
