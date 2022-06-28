package com.example.recipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_COUNT = "count";
    public static final String KEY_UNIT = "unit";

//    public Ingredient(String name, int count) {
//        this.setName(name);
//        this.setCount(count);
//    }
    public void initialize(String name, int count, String unit) {
        this.setName(name);
        this.setCount(count);
        this.setUnit(unit);
        return;
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getUnit() {
        return getString(KEY_UNIT);
    }

    public void setUnit(String unit) {
        put(KEY_UNIT, unit);
    }
    public int getCount() {
        return getInt(KEY_COUNT);
    }
    public void setCount(int count) {
        put(KEY_COUNT,count);
    }
}

