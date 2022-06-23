package com.example.recipeapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.adapters.RecipeSearchAdapter;

import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class RecipeSearchFragment extends Fragment {
    public static final String TAG = "RecipeSearchFragment";
    private FragmentRecipeSearchBinding binding;
    public List<Recipe> recipes;
    protected RecipeSearchAdapter adapter;


    public RecipeSearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipes = new ArrayList<>();
        adapter = new RecipeSearchAdapter(getContext(),recipes);
        binding.rvRecipes.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        // TODO: Implement Endless Scrolling and Refresh
        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query);
                try {
                    getRecipes(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.i(TAG, newText);
//                try {
//                    getRecipes(newText);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return true;

            }
        });
    }

    private void getRecipes(String query) throws IOException {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        headers.put("X-RapidAPI-Key", getString(R.string.Nutrition_API_Key));
        headers.put("X-RapidAPI-Host","spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");

        RequestParams params = new RequestParams();
        params.put("query",query);
        params.put("addRecipeInformation","true");
        params.put("instructionsRequired","true");
        params.put("sortDirection","asc");


        client.get("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/complexSearch",headers,params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG,"Results: " + results.toString());
                    recipes = Recipe.getRecipes(results);
                    adapter.clear();
                    adapter.addAll(recipes);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception");
                }

            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG,"onFailure");
            }
        });
    }
}