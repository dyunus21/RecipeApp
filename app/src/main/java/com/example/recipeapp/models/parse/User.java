package com.example.recipeapp.models.parse;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
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
    public static final String KEY_INGREDIENT_ARRAY = "ingredientsArray";
    public static final String KEY_INGREDIENTS_STRING = "ingredientsString";
    public static final String KEY_RECIPES_UPLOADED = "recipesUploaded";
    public static final String KEY_RECIPES_MADE = "recipesMade";
    public static final String KEY_RECIPES_LIKED = "recipesLiked";
    private static final String TAG = "User";
    private ParseUser parseUser;

    public User() {
    }

    public User(@NonNull final ParseUser user) {
        this.parseUser = user;
    }

    public static void getUser(@NonNull final User user) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include(User.KEY_INGREDIENT_ARRAY);
        query.include(User.KEY_INGREDIENTS_STRING);
        query.include(User.KEY_PROFILE_IMAGE);
        query.include(User.KEY_RECIPES_LIKED);
        query.include(User.KEY_RECIPES_MADE);
        query.include(User.KEY_RECIPES_UPLOADED);
        query.getInBackground(currentUser.getObjectId(), (object, e) -> {
            if (e != null) {
                Log.i(TAG, "Unable to fetch user!", e);
                return;
            }
            Log.i(TAG, "Successfully fetched user!");
            user.parseUser = object;
        });
    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public String getFirstName() {
        return parseUser.getString(KEY_FIRSTNAME);
    }

    public void setFirstName(@NonNull final String firstName) {
        parseUser.put(KEY_FIRSTNAME, firstName);
    }

    public String getLastName() {
        return parseUser.getString(KEY_LASTNAME);
    }

    public void setLastName(@NonNull final String lastName) {
        parseUser.put(KEY_LASTNAME, lastName);
    }

    public ParseFile getProfileImage() {
        return parseUser.getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(@NonNull final ParseFile image) {
        parseUser.put(KEY_PROFILE_IMAGE, image);
    }

    public String getIngredientsString() {
        String ingredients = "";
        final List<Ingredient> ingredientList = this.getIngredientArray();
        for (int i = 0; i < ingredientList.size(); i++) {
            ingredients += ingredientList.get(i).getName();
            if (i != ingredientList.size() - 1)
                ingredients += ",";
        }
        return ingredients;
    }

    public List<Ingredient> getIngredientArray() {
        final List<Ingredient> ingredientList = parseUser.getList(KEY_INGREDIENT_ARRAY);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }


    public void setIngredientArray(@NonNull final List<Ingredient> ingredientList) {
        parseUser.put(KEY_INGREDIENT_ARRAY, ingredientList);

    }

    public List<Recipe> getRecipesUploaded() {
        final List<Recipe> recipesUploaded = parseUser.getList(KEY_RECIPES_UPLOADED);
        if (recipesUploaded == null)
            return new ArrayList<>();
        return recipesUploaded;
    }

    public void setRecipesUploaded(@NonNull final List<Recipe> recipesUploaded) {
        parseUser.put(KEY_RECIPES_UPLOADED, recipesUploaded);
    }

    public List<Recipe> getRecipesMade() {
        final List<Recipe> recipesMade = parseUser.getList(KEY_RECIPES_MADE);
        if (recipesMade == null)
            return new ArrayList<>();
        return recipesMade;
    }

    public void setRecipesMade(@NonNull final List<Recipe> recipesMade) {
        parseUser.put(KEY_RECIPES_MADE, recipesMade);
    }

    public List<Recipe> getRecipesLiked() {
        final List<Recipe> recipesLiked = parseUser.getList(KEY_RECIPES_LIKED);
        if (recipesLiked == null)
            return new ArrayList<>();
        return recipesLiked;
    }

    public void setRecipesLiked(@NonNull final List<Recipe> recipesLiked) {
        parseUser.put(KEY_RECIPES_LIKED, recipesLiked);
    }

    public boolean isLikedbyCurrentUser(@NonNull final Recipe currentRecipe) {
        for (final Recipe recipe : getRecipesLiked()) {
            if (recipe.hasSameId(currentRecipe)) {
                Log.i(TAG, "Recipe is already liked by " + getParseUser().getUsername());
                return true;
            }
        }
        Log.i(TAG, "Recipe has been liked by " + getParseUser().getUsername());
        return false;
    }

    public void likeRecipe(@NonNull final Recipe recipe) {
        final List<Recipe> recipesLiked = getRecipesLiked();
        for (int i = 0; i < recipesLiked.size(); i++) {
            if (recipesLiked.get(i).hasSameId(recipe)) {
                recipesLiked.remove(i);
                Log.i(TAG, "Size: " + recipesLiked.size());
                setRecipesLiked(recipesLiked);
                return;
            }
        }
        recipesLiked.add(0,recipe);
        setRecipesLiked(recipesLiked);
    }

    public boolean isMadebyCurrentUser(@NonNull final Recipe currentRecipe) {
        for (final Recipe recipe : getRecipesMade()) {
            if (recipe.hasSameId(currentRecipe)) {
                Log.i(TAG, "Recipe is already made by " + getParseUser().getUsername());
                return true;
            }
        }
        Log.i(TAG, "Recipe has not been made by " + getParseUser().getUsername());
        return false;
    }

    public void madeRecipe(@NonNull final Recipe recipe) {
        final List<Recipe> recipesMade = getRecipesMade();
        for (int i = 0; i < recipesMade.size(); i++) {
            if (recipesMade.get(i).hasSameId(recipe)) {
                recipesMade.remove(i);
                Log.i(TAG, "Size: " + recipesMade.size());
                setRecipesMade(recipesMade);
                return;
            }
        }
        recipesMade.add(0,recipe);
        setRecipesMade(recipesMade);
    }


}
