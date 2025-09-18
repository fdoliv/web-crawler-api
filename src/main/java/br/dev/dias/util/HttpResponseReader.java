package br.dev.dias.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

/**
 * Utility class for reading HTTP responses from an HttpURLConnection.
 * Handles both successful and error responses.
 */
public class HttpResponseReader {

    /**
     * Reads the response from the given HttpURLConnection.
     * If the response code indicates success (2xx), reads from the input stream.
     * Otherwise, reads from the error stream.
     *
     * @param connection the HttpURLConnection to read the response from
     * @return the response content as a String
     * @throws IOException if an I/O error occurs while reading the response
     */
    public String readResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n")).trim();
        }
    }
}
