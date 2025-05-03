package com.axreng.backend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

public class HttpResponseReader {

     public String readResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
