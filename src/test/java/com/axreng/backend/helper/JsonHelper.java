package com.axreng.backend.helper;

import java.util.Map;

import com.google.gson.Gson;

public class JsonHelper {
    private static final Gson GSON = new Gson();

    public static String extractFieldFromJson(String json, String field) {
        return GSON.fromJson(json, Map.class).get(field).toString();
    }
}
