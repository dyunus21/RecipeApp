package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.RecipeClient;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class RecipeSearchFragment extends Fragment {
    public static final String TAG = "RecipeSearchFragment";
    public List<Recipe> recipes;
    protected RecipeSearchAdapter adapter;
    private FragmentRecipeSearchBinding binding;
    private RecipeClient client;


    public RecipeSearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new RecipeClient(getContext());
        recipes = new ArrayList<>();
        adapter = new RecipeSearchAdapter(getContext(), recipes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvRecipes.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        // TODO: Implement Endless Scrolling and Refresh
        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query);
                try {
                    populateRecipes(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            // Note to self: Getting recipes everytime query changes might result in extensive API calls
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

    public void populateRecipes(String query) throws IOException {
        User user = new User(ParseUser.getCurrentUser());
        client.getRecipes(query,user.getIngredientStringList(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("results");
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
                try {
                    recipes = Recipe.getRecipes(jsonArray);
                    adapter.clear();
                    adapter.addAll(recipes);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });
    }
}