package com.slotegrator.assignment.config;

import java.net.URI;
import java.util.Optional;

public final class TestConfig {
    private static final String DEFAULT_BASE_URL = "https://testslotegrator.com";
    private static final String DEFAULT_CURRENCY = "USD";

    private TestConfig() {
    }

    public static URI baseUri() {
        return URI.create(value("BASE_URL", DEFAULT_BASE_URL));
    }

    public static String testerEmail() {
        return required("TESTER_EMAIL");
    }

    public static String testerPassword() {
        return required("TESTER_PASSWORD");
    }

    public static String defaultCurrency() {
        return value("DEFAULT_CURRENCY", DEFAULT_CURRENCY);
    }

    private static String required(String key) {
        return Optional.ofNullable(System.getenv(key))
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalStateException("Environment variable " + key + " is required"));
    }

    private static String value(String key, String fallback) {
        return Optional.ofNullable(System.getenv(key))
                .filter(value -> !value.isBlank())
                .orElse(fallback);
    }
}
