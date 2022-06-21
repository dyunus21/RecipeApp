package com.example.recipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_FOLLOWER_LIST = "followerList";
    public static final String KEY_FOLLOWING_LIST = "followingList";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_RECIPES_UPLOADED = "recipesUploaded";
    public static final String KEY_RECIPES_MADE = "recipesMade";
    public static final String KEY_POSTS_LIKED = "postsLiked";

    public String getFirstName() {
        return getString(KEY_FIRSTNAME);
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRSTNAME, firstName);
    }

    public String getLastName() {
        return getString(KEY_LASTNAME);
    }

    public void setLastName(String lastName) {
        put(KEY_LASTNAME, lastName);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile image) {
        put(KEY_PROFILE_IMAGE, image);
    }

    public List<User> getFollowerList() {
        List<User> followerList = getList(KEY_FOLLOWER_LIST);
        if (followerList == null)
            return new ArrayList<>();
        return followerList;
    }

    public void setFollowerList(List<User> followerList) {
        put(KEY_FOLLOWER_LIST, followerList);
    }

    public List<User> getFollowingList() {
        List<User> followingList = getList(KEY_FOLLOWING_LIST);
        if (followingList == null)
            return new ArrayList<>();
        return followingList;
    }

    public void setFollowingList(List<User> followingList) {
        put(KEY_FOLLOWING_LIST, followingList);
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

    public List<Recipe> getRecipesUploaded() {
        List<Recipe> recipesUploaded = getList(KEY_RECIPES_UPLOADED);
        if (recipesUploaded == null)
            return new ArrayList<>();
        return recipesUploaded;
    }

    public void setRecipesUploaded(List<Recipe> recipesUploaded) {
        put(KEY_RECIPES_UPLOADED, recipesUploaded);
    }

    public List<Recipe> getRecipesMade() {
        List<Recipe> recipesMade = getList(KEY_RECIPES_MADE);
        if (recipesMade == null)
            return new ArrayList<>();
        return recipesMade;
    }

    public void setRecipesMade(List<Recipe> recipesMade) {
        put(KEY_RECIPES_MADE, recipesMade);
    }

    public List<Post> postsLiked() {
        List<Post> postsLiked = getList(KEY_POSTS_LIKED);
        if (postsLiked == null)
            return new ArrayList<>();
        return postsLiked;
    }

    public void setPostsLiked(List<Post> postsLiked) {
        put(KEY_POSTS_LIKED, postsLiked);
    }

}
