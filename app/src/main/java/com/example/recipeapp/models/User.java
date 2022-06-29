package com.example.recipeapp.models;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_FOLLOWER_LIST = "followerList";
    public static final String KEY_FOLLOWING_LIST = "followingList";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_INGREDIENT_ARRAY = "ingredientsArray";
    public static final String KEY_INGREDIENTS_STRING = "ingredientsString";
    public static final String KEY_RECIPES_UPLOADED = "recipesUploaded";
    public static final String KEY_RECIPES_MADE = "recipesMade";
    public static final String KEY_POSTS_LIKED = "postsLiked";
    private static final String TAG = "User";
    private ParseUser parseUser;

    public User() {
    }

    public User(ParseUser user) {
        this.parseUser = user;
    }

    public static void getUser(User user) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include(User.KEY_INGREDIENT_ARRAY);
        query.include(User.KEY_INGREDIENTS_STRING);
        query.include(User.KEY_PROFILE_IMAGE);
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Unable to fetch user!", e);
                }
                Log.i(TAG, "Successfully fetched user!");
                user.parseUser = object;
            }
        });
    }

    //TODO: Create a constructor that sets all fields
    public ParseUser getParseUser() {
        return parseUser;
    }

    public String getFirstName() {
        return parseUser.getString(KEY_FIRSTNAME);
    }

    public void setFirstName(String firstName) {
        parseUser.put(KEY_FIRSTNAME, firstName);
    }

    public String getLastName() {
        return parseUser.getString(KEY_LASTNAME);
    }

    public void setLastName(String lastName) {
        parseUser.put(KEY_LASTNAME, lastName);
    }

    public ParseFile getProfileImage() {
        return parseUser.getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile image) {
        parseUser.put(KEY_PROFILE_IMAGE, image);
    }

    public List<User> getFollowerList() {
        List<User> followerList = parseUser.getList(KEY_FOLLOWER_LIST);
        if (followerList == null)
            return new ArrayList<>();
        return followerList;
    }

    public void setFollowerList(List<User> followerList) {
        parseUser.put(KEY_FOLLOWER_LIST, followerList);
    }

    public List<User> getFollowingList() {
        List<User> followingList = parseUser.getList(KEY_FOLLOWING_LIST);
        if (followingList == null)
            return new ArrayList<>();
        return followingList;
    }

    public void setFollowingList(List<User> followingList) {
        parseUser.put(KEY_FOLLOWING_LIST, followingList);
    }

    public String getIngredientsString() {
        return parseUser.getString(KEY_INGREDIENTS_STRING);
    }

    public void setIngredientsString(String ingredients) {
        parseUser.put(KEY_INGREDIENTS_STRING, ingredients);
    }

    public List<Ingredient> getIngredientArray() {
        List<Ingredient> ingredientList = parseUser.getList(KEY_INGREDIENT_ARRAY);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }


    public void setIngredientArray(List<Ingredient> ingredientList) {
        parseUser.put(KEY_INGREDIENT_ARRAY, ingredientList);

    }

    public List<Recipe> getRecipesUploaded() {
        List<Recipe> recipesUploaded = parseUser.getList(KEY_RECIPES_UPLOADED);
        if (recipesUploaded == null)
            return new ArrayList<>();
        return recipesUploaded;
    }

    public void setRecipesUploaded(List<Recipe> recipesUploaded) {
        parseUser.put(KEY_RECIPES_UPLOADED, recipesUploaded);
    }

    public List<Recipe> getRecipesMade() {
        List<Recipe> recipesMade = parseUser.getList(KEY_RECIPES_MADE);
        if (recipesMade == null)
            return new ArrayList<>();
        return recipesMade;
    }

    public void setRecipesMade(List<Recipe> recipesMade) {
        parseUser.put(KEY_RECIPES_MADE, recipesMade);
    }

    public List<Post> postsLiked() {
        List<Post> postsLiked = parseUser.getList(KEY_POSTS_LIKED);
        if (postsLiked == null)
            return new ArrayList<>();
        return postsLiked;
    }

    public void setPostsLiked(List<Post> postsLiked) {
        parseUser.put(KEY_POSTS_LIKED, postsLiked);
    }

}
