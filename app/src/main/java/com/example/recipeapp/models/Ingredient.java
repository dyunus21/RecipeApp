package com.example.recipeapp.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_COUNT = "count";
    public static final String KEY_UNIT = "unit";

    public void initialize(@NonNull final String name, final int count, @NonNull final String unit) {
        this.setName(name);
        this.setCount(count);
        this.setUnit(unit);
    }

    @Nullable
    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(@NonNull final String name) {
        put(KEY_NAME, name);
    }

    @Nullable
    public String getUnit() {
        return getString(KEY_UNIT);
    }

    public void setUnit(@NonNull final String unit) {
        put(KEY_UNIT, unit);
    }

    public int getCount() {
        return getInt(KEY_COUNT);
    }

    public void setCount(final int count) {
        put(KEY_COUNT, count);
    }
}

