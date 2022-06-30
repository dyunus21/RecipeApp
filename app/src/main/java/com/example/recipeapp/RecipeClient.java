package com.example.recipeapp;

import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;

public class RecipeClient {
    public static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    public static final String HOST = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    private static final String TAG = "RecipeClient";
    private final RequestHeaders headers;
    private final Context context;

    public RecipeClient(Context context) {
        this.context = context;
        headers = new RequestHeaders();
        headers.put("X-RapidAPI-Key", context.getString(R.string.Nutrition_API_Key));
        headers.put("X-RapidAPI-Host", HOST);
    }

    public void getRecipes(String query, Map<String, String> parameters, JsonHttpResponseHandler handler) throws IOException {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("addRecipeInformation", "true");
        params.put("instructionsRequired", "true");
        params.put("sortDirection", "asc");
        params.put("fillIngredients", "true");
//        params.put("includeIngredients", parameters.get("Ingredients"));
        if(parameters.size() > 1) {
            if (!Objects.equals(parameters.get("Cuisine"), ""))
                params.put("cuisine", parameters.get("Cuisine"));
            if (!Objects.equals(parameters.get("MealType"), ""))
                params.put("type", parameters.get("MealType"));
            if (!Objects.equals(parameters.get("Cooktime"), ""))
                params.put("maxReadyTime", parameters.get("Cooktime"));
        }

        client.get(BASE_URL + "/recipes/complexSearch", headers, params, handler);
    }

    // Currently only used to extract ingredient list
    public void getRecipesDetailed(int recipeId, JsonHttpResponseHandler handler) throws IOException {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("id", recipeId);
        Log.i(TAG, "RecipeDetailedURL: " + BASE_URL + "/recipes/" + recipeId + "/information");
        client.get(BASE_URL + "/recipes/" + recipeId + "/information", headers, params, handler);
    }

}
