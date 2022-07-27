package com.example.recipeapp.clients;

import android.content.Context;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;

public class BarcodeAnalyzeClient {
    public static final String BASE_URL = "https://nutritionix-api.p.rapidapi.com/v1_1/item";
    public static final String HOST = "nutritionix-api.p.rapidapi.com";
    private static final String TAG = "NutritionixClient";
    private final RequestHeaders headers;
    private final Context context;

    public BarcodeAnalyzeClient(Context context) {
        this.context = context;
        headers = new RequestHeaders();
        headers.put("X-RapidAPI-Key", context.getString(R.string.Nutrition_API_Key));
        headers.put("X-RapidAPI-Host", HOST);
    }

    public void analyzeBarcode(String upc, JsonHttpResponseHandler handler) {
        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        params.put("upc", upc);
        client.get(BASE_URL, headers, params, handler);
    }
}
