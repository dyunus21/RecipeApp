package com.example.recipeapp.models.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_COOKTIME = "cooktime";
    public static final String KEY_CUISINE_TYPE = "cuisineType";
    public static final String KEY_SERVINGS = "servings";
    public static final String KEY_REVIEWS = "reviews";
    private static final String TAG = "Recipe";


    public int getRecipeId() {
        return getInt(KEY_RECIPE_ID);
    }

    public void setRecipeId(final int recipeId) {
        put(KEY_RECIPE_ID, recipeId);
    }

    public User getAuthor() {
        return new User(getParseUser(KEY_AUTHOR));
    }

    public void setAuthor(@NonNull final User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(@NonNull final String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(@Nullable final ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageUrl(@NonNull final String imageUrl) {
        put(KEY_IMAGE_URL, imageUrl);
    }

    public List<String> getIngredientList() {
        final List<String> ingredientList = getList(KEY_INGREDIENT_LIST);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }

    public void setIngredientList(@NonNull final List<String> ingredientList) {
        put(KEY_INGREDIENT_LIST, ingredientList);
    }

    public List<String> getInstructions() {
        final List<String> instructions = getList(KEY_INSTRUCTIONS);
        if (instructions == null)
            return new ArrayList<>();
        return instructions;
    }

    public void setInstructions(@NonNull final List<String> instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }

    public int getCooktime() {
        return getInt(KEY_COOKTIME);
    }

    public void setCooktime(final int cooktime) {
        put(KEY_COOKTIME, cooktime);
    }

    public String getCuisineType() {
        return getString(KEY_CUISINE_TYPE);
    }

    public void setCuisineType(final String cuisineType) {
        put(KEY_CUISINE_TYPE, cuisineType);
    }

    public int getServings() {
        return getInt(KEY_SERVINGS);
    }

    public void setServings(final int servings) {
        put(KEY_SERVINGS, servings);
    }
}

