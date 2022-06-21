package com.example.recipeapp.models;

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
    public static final String KEY_MEDIA = "media";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_COOKTIME = "cooktime";
    public static final String KEY_CUISINE_TYPE = "cuisineType";
    public static final String KEY_REVIEWS = "reviews";

    public int getRecipeId() {
        return getInt(KEY_RECIPE_ID);
    }

    public void setRecipeId(int recipeId) {
        put(KEY_RECIPE_ID, recipeId);
    }

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(User author) {
        put(KEY_AUTHOR, author);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public List<ParseFile> getMedia() {
        List<ParseFile> media = getList(KEY_MEDIA);
        if (media == null)
            return new ArrayList<>();
        return media;
    }

    public void setMedia(List<ParseFile> media) {
        put(KEY_MEDIA, media);
    }

    public List<Ingredient> getIngredientList() {
        List<Ingredient> ingredientList = getList(KEY_INGREDIENT_LIST);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        put(KEY_INGREDIENT_LIST, ingredientList);
    }

    public List<String> getInstructions() {
        List<String> instructions = getList(KEY_INSTRUCTIONS);
        if (instructions == null)
            return new ArrayList<>();
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }

    public int getCooktime() {
        return getInt(KEY_COOKTIME);
    }

    public void setCooktime(int cooktime) {
        put(KEY_COOKTIME, cooktime);
    }

    public String getCuisineType() {
        return getString(KEY_CUISINE_TYPE);
    }

    public void setCuisineType(String cuisineType) {
        put(KEY_CUISINE_TYPE, cuisineType);
    }

    public List<Review> getReviews() {
        List<Review> reviews = getList(KEY_REVIEWS);
        if (reviews == null)
            return new ArrayList<>();
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        put(KEY_MEDIA, reviews);
    }

}
