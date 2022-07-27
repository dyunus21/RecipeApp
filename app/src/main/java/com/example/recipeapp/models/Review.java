package com.example.recipeapp.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Objects;

@ParseClassName("Review")
public class Review extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RATING = "rating";
    public static final String KEY_RECIPE = "recipe";
    private static final String TAG = "Review";

    public User getAuthor() {
        return new User(Objects.requireNonNull(getParseUser(KEY_AUTHOR)));
    }

    public void setAuthor(@NonNull final User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(@NonNull final String description) {
        put(KEY_DESCRIPTION, description);
    }

    public float getRating() {
        return (float) getDouble(KEY_RATING);
    }

    public void setRating(final double rating) {
        put(KEY_RATING, rating);
    }

    public Recipe getRecipe() {
        return (Recipe) getParseObject(KEY_RECIPE);
    }

    public void setRecipe(@NonNull final Recipe recipe) {
        put(KEY_RECIPE, recipe);
    }
}
