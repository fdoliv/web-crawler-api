package com.axreng.backend.util;

import java.util.UUID;


public class IDGenerator {

    private static final int ID_LENGTH = 8;


    public static String generateAlphanumericID() {
        return UUID.randomUUID()
                   .toString()
                   .replaceAll("-", "")
                   .substring(0, ID_LENGTH);
    }
}
