package com.fast0n.ap.java;

import java.util.Random;

public class GenerateToken {

    private static final String DATA = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

}