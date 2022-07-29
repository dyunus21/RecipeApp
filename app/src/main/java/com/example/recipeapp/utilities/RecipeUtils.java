package com.example.recipeapp.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.recipeapp.models.parse.Recipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipeUtils {

    private static final String TAG = "RecipeUtils";

    public RecipeUtils() {}

    @NonNull
    public List<Recipe> getRecipes(@NonNull final JSONArray results) throws JSONException {
        final List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            final Recipe recipe = new Recipe();
            recipe.setRecipeId(results.getJSONObject(i).getInt("id"));
            recipe.setTitle(results.getJSONObject(i).getString("title"));
            recipe.setCooktime(results.getJSONObject(i).getInt("readyInMinutes"));
            final JSONArray cuisineType = results.getJSONObject(i).getJSONArray("cuisines");
            if (cuisineType.length() > 0)
                recipe.setCuisineType(cuisineType.getString(0));
            else
                recipe.setCuisineType("None");

            if (results.getJSONObject(i).getJSONArray("analyzedInstructions").length() == 0) {
                continue;
            }
            final List<String> instructions = new ArrayList<>();
            final JSONArray steps = (results.getJSONObject(i).getJSONArray("analyzedInstructions")).getJSONObject(0).getJSONArray("steps");
            for (int j = 0; j < steps.length(); j++) {
                instructions.add(steps.getJSONObject(j).getString("step"));
            }
            recipe.setInstructions(instructions);

            recipe.setImageUrl(results.getJSONObject(i).getString("image"));
            recipe.setServings(results.getJSONObject(i).getInt("servings"));
            Log.i(TAG, "Added " + recipe.getTitle());
            recipes.add(recipe);

        }
        return recipes;
    }
}
