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

import okhttp3.Headers;

public class RecipeClient {
    public static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    public static final String HOST = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    private static final String TAG = "RecipeClient";
    private RequestHeaders headers;
    private Context context;

    public RecipeClient(Context context) {
        this.context = context;
        headers = new RequestHeaders();
        headers.put("X-RapidAPI-Key", context.getString(R.string.Nutrition_API_Key) );
        headers.put("X-RapidAPI-Host", HOST);
    }

    public void getRecipes(String query, JsonHttpResponseHandler handler) throws IOException {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("addRecipeInformation", "true");
        params.put("instructionsRequired", "true");
        params.put("sortDirection", "asc");

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
