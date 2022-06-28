package com.example.recipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_COUNT = "count";

    public Ingredient(String name, int count) {
        this.setName(name);
        this.setCount(count);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public int getCount() {
        return getInt(KEY_COUNT);
    }
    public void setCount(int count) {
        put(KEY_COUNT,count);
    }
}

