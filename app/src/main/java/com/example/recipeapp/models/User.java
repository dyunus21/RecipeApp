package com.example.recipeapp.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class User {
    private ParseUser user;
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_FOLLOWER_LIST = "followerList";
    public static final String KEY_FOLLOWING_LIST = "followingList";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_RECIPES_UPLOADED = "recipesUploaded";
    public static final String KEY_RECIPES_MADE = "recipesMade";
    public static final String KEY_POSTS_LIKED = "postsLiked";

    public User(ParseUser user) {
        this.user = user;
    }
    //Create a constructor that sets all fields
    public ParseUser getParseUser() {
        return user;
    }

    public String getFirstName() {
        return user.getString(KEY_FIRSTNAME);
    }

    public void setFirstName(String firstName) {
        user.put(KEY_FIRSTNAME, firstName);
    }

    public String getLastName() {
        return user.getString(KEY_LASTNAME);
    }

    public void setLastName(String lastName) {
        user.put(KEY_LASTNAME, lastName);
    }

    public ParseFile getProfileImage() {
        return user.getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile image) {
        user.put(KEY_PROFILE_IMAGE, image);
    }

    public List<User> getFollowerList() {
        List<User> followerList = user.getList(KEY_FOLLOWER_LIST);
        if (followerList == null)
            return new ArrayList<>();
        return followerList;
    }

    public void setFollowerList(List<User> followerList) {
        user.put(KEY_FOLLOWER_LIST, followerList);
    }

    public List<User> getFollowingList() {
        List<User> followingList = user.getList(KEY_FOLLOWING_LIST);
        if (followingList == null)
            return new ArrayList<>();
        return followingList;
    }

    public void setFollowingList(List<User> followingList) {
        user.put(KEY_FOLLOWING_LIST, followingList);
    }

    public List<Ingredient> getIngredientList() {
        List<Ingredient> ingredientList = user.getList(KEY_INGREDIENT_LIST);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }

    public String getIngredientStringList() {
        List<Ingredient> ingredientList = this.getIngredientList();
        String ingredients = "";
        for(int i = 0; i<ingredientList.size();i++) {
            ingredients += ingredientList.get(i).getName();
            if(i != ingredientList.size()-1) {
                ingredients+=",";
            }
        }
        return ingredients;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        user.put(KEY_INGREDIENT_LIST, ingredientList);
    }

    public List<Recipe> getRecipesUploaded() {
        List<Recipe> recipesUploaded = user.getList(KEY_RECIPES_UPLOADED);
        if (recipesUploaded == null)
            return new ArrayList<>();
        return recipesUploaded;
    }

    public void setRecipesUploaded(List<Recipe> recipesUploaded) {
        user.put(KEY_RECIPES_UPLOADED, recipesUploaded);
    }

    public List<Recipe> getRecipesMade() {
        List<Recipe> recipesMade = user.getList(KEY_RECIPES_MADE);
        if (recipesMade == null)
            return new ArrayList<>();
        return recipesMade;
    }

    public void setRecipesMade(List<Recipe> recipesMade) {
        user.put(KEY_RECIPES_MADE, recipesMade);
    }

    public List<Post> postsLiked() {
        List<Post> postsLiked = user.getList(KEY_POSTS_LIKED);
        if (postsLiked == null)
            return new ArrayList<>();
        return postsLiked;
    }

    public void setPostsLiked(List<Post> postsLiked) {
        user.put(KEY_POSTS_LIKED, postsLiked);
    }

}
