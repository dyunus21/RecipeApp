package com.example.recipeapp.utilities;

public class CurrentTimeProvider {
    public static final String TAG = "CurrentTimeProvider";

    public CurrentTimeProvider() {

    }
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
