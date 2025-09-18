package br.dev.dias.util;

/**
 * Utility class that defines constants for common HTTP response status codes.
 */
public class HttpResponseCode {

    /**
     * Private constructor to prevent instantiation.
     */
    private HttpResponseCode() {}

    /**
     * HTTP 200 OK.
     */
    public static final int OK = 200;

    /**
     * HTTP 201 Created.
     */
    public static final int CREATED = 201;

    /**
     * HTTP 202 Accepted.
     */
    public static final int ACCEPTED = 202;

    /**
     * HTTP 204 No Content.
     */
    public static final int NO_CONTENT = 204;

    /**
     * HTTP 301 Moved Permanently.
     */
    public static final int MOVED_PERMANENTLY = 301;

    /**
     * HTTP 302 Found.
     */
    public static final int FOUND = 302;

    /**
     * HTTP 304 Not Modified.
     */
    public static final int NOT_MODIFIED = 304;

    /**
     * HTTP 400 Bad Request.
     */
    public static final int BAD_REQUEST = 400;

    /**
     * HTTP 401 Unauthorized.
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * HTTP 403 Forbidden.
     */
    public static final int FORBIDDEN = 403;

    /**
     * HTTP 404 Not Found.
     */
    public static final int NOT_FOUND = 404;

    /**
     * HTTP 405 Method Not Allowed.
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * HTTP 409 Conflict.
     */
    public static final int CONFLICT = 409;

    /**
     * HTTP 500 Internal Server Error.
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * HTTP 501 Not Implemented.
     */
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * HTTP 502 Bad Gateway.
     */
    public static final int BAD_GATEWAY = 502;

    /**
     * HTTP 503 Service Unavailable.
     */
    public static final int SERVICE_UNAVAILABLE = 503;
}
