package com.robin.swanhack25;


public class SessionManager {


    public static int getKeyUserId() {
        return KEY_USER_ID;
    }

    public static String getKeyUsername() {
        return KEY_USERNAME;
    }

    public static void setKeyUserId(int keyUserId) {
        KEY_USER_ID = keyUserId;
    }

    public static void setKeyUsername(String keyUsername) {
        KEY_USERNAME = keyUsername;
    }

    public static void clear() {
        KEY_USER_ID = 0;
        KEY_USERNAME = null;
    }

    private static int KEY_USER_ID;
    private static String KEY_USERNAME;









}

