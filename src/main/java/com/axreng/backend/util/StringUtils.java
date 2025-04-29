package com.axreng.backend.util;

public class StringUtils {

    private StringUtils() {
        // Private constructor to prevent instantiation
    }

    public static String escapeJson(String value) {
        return value == null ? null : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}