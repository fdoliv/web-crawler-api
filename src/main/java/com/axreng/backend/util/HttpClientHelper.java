package com.axreng.backend.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientHelper {

    private HttpClientHelper() {}

    private static HttpClientHelper instance;

    public static HttpClientHelper getInstance() {
        if (instance == null) {
            instance = new HttpClientHelper();
        }
        return instance;
    }
    public HttpURLConnection createConnection(String url, String method, String body) throws Exception {
        URL endpoint = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (body != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }
        }

        return connection;
    }
}
