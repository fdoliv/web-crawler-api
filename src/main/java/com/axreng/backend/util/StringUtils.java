package com.axreng.backend.util;

/**
 * Utility class for string operations.
 * Provides methods for escaping special characters in strings.
 */
public class StringUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private StringUtils() {}

    /**
     * Escapes special characters in a string for safe use in JSON.
     * Replaces backslashes and double quotes with their escaped equivalents.
     *
     * @param value the string to escape
     * @return the escaped string, or null if the input is null
     */
    public static String escapeJson(String value) {
        return value == null ? null : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}