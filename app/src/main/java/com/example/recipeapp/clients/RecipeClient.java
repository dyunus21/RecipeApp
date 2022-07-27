package com.example.recipeapp.clients;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public class RecipeClient {
    public static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    public static final String HOST = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
    private static final String TAG = "RecipeClient";
    @NonNull
    private final RequestHeaders headers;
    @NonNull
    private final AsyncHttpClient client = new AsyncHttpClient();

    public RecipeClient(@NonNull final Context context) {
        headers = new RequestHeaders();
        headers.put("X-RapidAPI-Key", context.getString(R.string.Nutrition_API_Key));
        headers.put("X-RapidAPI-Host", HOST);
    }

    public void getRecipes(final String query, final Map<String, String> parameters, @NonNull final JsonHttpResponseHandler handler) {
        final RequestParams params = new RequestParams();
        params.put("query", query);
        params.put("addRecipeInformation", "true");
        params.put("instructionsRequired", "true");
        params.put("sortDirection", "asc");
        params.put("fillIngredients", "true");

        if (parameters.size() > 1) {
            if (!Objects.equals(parameters.get("Cuisine"), ""))
                params.put("cuisine", parameters.get("Cuisine"));
            if (!Objects.equals(parameters.get("MealType"), ""))
                params.put("type", parameters.get("MealType"));
            if (!Objects.equals(parameters.get("Cooktime"), ""))
                params.put("maxReadyTime", parameters.get("Cooktime"));
            if (Objects.equals(parameters.get("switchIngredients"), "true"))
                params.put("includeIngredients", parameters.get("Ingredients"));
        }

        client.get(BASE_URL + "/recipes/complexSearch", headers, params, handler);
    }

    public void getRecipesDetailed(final int recipeId, @NonNull final JsonHttpResponseHandler handler) {
        final RequestParams params = new RequestParams();
        params.put("id", recipeId);
        Log.i(TAG, "RecipeDetailedURL: " + BASE_URL + "/recipes/" + recipeId + "/information");
        client.get(BASE_URL + "/recipes/" + recipeId + "/information", headers, params, handler);
    }

    public void getRandomRecipes(@NonNull final JsonHttpResponseHandler handler) {
        final RequestParams params = new RequestParams();
        params.put("number", 10);
        client.get(BASE_URL + "/recipes/random", headers, params, handler);
    }


    public void getRecipesByImage(@NonNull final File photoFile, @NonNull final JsonHttpResponseHandler handler) {
        final RequestParams params = new RequestParams();
        try {
            final BufferedSource bufferedSource = Okio.buffer(Okio.source(photoFile));
            final ByteString source = bufferedSource.readByteString();
            final RequestBody body = RequestBody.create(source.toByteArray(), MediaType.get("image/jpg"));
            final RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "photo.jpg", body)
                    .build();
            client.post(BASE_URL + "/food/images/analyze", headers, params, requestBody, handler);
        } catch (IOException e) {
            Log.e(TAG, "Unable to search recipes based on provided image!", e);
        }

    }

    public void getRecipeInformationBulk(final String ids, @NonNull final JsonHttpResponseHandler handler) {
        final RequestParams params = new RequestParams();
        params.put("ids", ids);
        client.get(BASE_URL + "/recipes/informationBulk", headers, params, handler);
    }
}
