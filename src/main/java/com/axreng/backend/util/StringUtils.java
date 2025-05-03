package com.axreng.backend.util;

public class StringUtils {

    private StringUtils() {}

    public static String escapeJson(String value) {
        return value == null ? null : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}