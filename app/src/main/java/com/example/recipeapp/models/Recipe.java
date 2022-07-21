package com.example.recipeapp.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_INGREDIENT_LIST = "ingredientList";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_COOKTIME = "cooktime";
    public static final String KEY_CUISINE_TYPE = "cuisineType";
    public static final String KEY_REVIEWS = "reviews";
    private static final String TAG = "Recipe";

    public static List<Recipe> getRecipes(JSONArray results) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            Recipe recipe = new Recipe();
            recipe.setRecipeId(results.getJSONObject(i).getInt("id"));
            recipe.setTitle(results.getJSONObject(i).getString("title"));
            recipe.setCooktime(results.getJSONObject(i).getInt("readyInMinutes"));
            JSONArray cuisineType = results.getJSONObject(i).getJSONArray("cuisines");
            if (cuisineType.length() > 0)
                recipe.setCuisineType(cuisineType.getString(0));
            else
                recipe.setCuisineType("None");

            if (results.getJSONObject(i).getJSONArray("analyzedInstructions").length() == 0) {
                continue;
            }
            List<String> instructions = new ArrayList<>();
            JSONArray steps = (results.getJSONObject(i).getJSONArray("analyzedInstructions")).getJSONObject(0).getJSONArray("steps");
            for (int j = 0; j < steps.length(); j++) {
                instructions.add(steps.getJSONObject(j).getString("step"));
            }
            recipe.setInstructions(instructions);

            if (results.getJSONObject(i).getString("image") == "") {
                recipe.setImage(null);
            }
            recipe.setImageUrl(results.getJSONObject(i).getString("image"));
            Log.i(TAG, "Added " + recipe.getTitle());
            recipes.add(recipe);

        }
        return recipes;
    }

    public static Recipe addIngredients(Recipe recipe, List<String> ingredients) throws JSONException {
        recipe.setIngredientList(ingredients);
        Log.i(TAG, "Rec: " + recipe.getIngredientList().toString());
        return recipe;
    }

    public static byte[] encodeToByteArray(Bitmap image) {
        Log.d(TAG, "encodeToByteArray");
        Bitmap b = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgByteArray = baos.toByteArray();
        return imgByteArray;
    }

    public int getRecipeId() {
        return getInt(KEY_RECIPE_ID);
    }

    public void setRecipeId(int recipeId) {
        put(KEY_RECIPE_ID, recipeId);
    }

    public User getAuthor() {
        return new User(getParseUser(KEY_AUTHOR));
    }

    public void setAuthor(User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(KEY_IMAGE_URL, imageUrl);
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

    public List<String> getIngredientList() {
        List<String> ingredientList = getList(KEY_INGREDIENT_LIST);
        if (ingredientList == null)
            return new ArrayList<>();
        return ingredientList;
    }

    public void setIngredientList(List<String> ingredientList) {
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

    public File resizeFile(Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = new File("photo.jpg");
        try {
            resizedFile.createNewFile();
            FileOutputStream fos = null;
            fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to create new file ", e);
        }
        Log.i(TAG, "File: " + resizedFile);
        return resizedFile;
    }

    public boolean isRecipeStored() {
        final boolean[] result = {false};
        ParseQuery<Recipe> query = ParseQuery.getQuery("Recipe");
        query.whereEqualTo(Recipe.KEY_TITLE, getTitle());
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    Log.i(TAG, "Recipe found in database!");
                    result[0] = true;
                }
            }
        });
        return result[0];
    }
}

