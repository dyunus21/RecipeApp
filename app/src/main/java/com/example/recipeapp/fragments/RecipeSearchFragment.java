package com.example.recipeapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeClient;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class RecipeSearchFragment extends Fragment {
    public static final String TAG = "RecipeSearchFragment";
    private final Map<String, String> params = new HashMap<>();
    public List<Recipe> recipes;
    protected RecipeSearchAdapter adapter;
    private FragmentRecipeSearchBinding binding;
    private RecipeClient client;
    private User currentUser;


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
        getUser();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvRecipes.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        // TODO: Implement Endless Scrolling and Refresh
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    initializeScreen();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to initialize screen");
                }
            }
        });
        try {
            initializeScreen();
        } catch (IOException e) {
            Log.e(TAG, "Unable to initialize screen");
        }
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        binding.ibFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.filter_dialog, null);
                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getContext());
                final AutoCompleteTextView actvCuisine = view.findViewById(R.id.actvCuisine);
                ArrayAdapter cuisineAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cuisine));
                actvCuisine.setAdapter(cuisineAdapter);

                final AutoCompleteTextView actvMealType = view.findViewById(R.id.actvMealType);
                ArrayAdapter mealAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.meal));
                actvMealType.setAdapter(mealAdapter);

                final EditText etCooktime = view.findViewById(R.id.etCooktime);
                alertDialog.setTitle("Choose your preferences");

                alertDialog.setPositiveButton("Set Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        params.put("Cooktime", etCooktime.getText().toString());
                        params.put("Cuisine", actvCuisine.getText().toString());
                        params.put("MealType", actvMealType.getText().toString());

                        Log.i(TAG, "Max Cooktime: " + etCooktime.getText().toString());
                        Log.i(TAG, "Cuisine text: " + actvCuisine.getText());
                        Log.i(TAG, "Meal type text: " + actvMealType.getText());
                        Log.i(TAG, params.toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Cancelled filter");
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();


            }
        });
        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query);
                try {
                    adapter.clear();
                    populateRecipes(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
    }

    private void initializeScreen() throws IOException {

        // TODO: if pull to refresh, change random recipes
        client.getRandomRecipes(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successfully initialized search screen " + json.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("recipes");
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
                try {
                    adapter.clear();
                    recipes = Recipe.getRecipes(jsonArray);
                    adapter.addAll(recipes);
                    binding.rvRecipes.scrollToPosition(0);
                    binding.swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    private void showNoResultsDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setMessage("No results found!");
        materialAlertDialogBuilder.show();
    }

    public void populateRecipes(String query) throws IOException {

        ParseQuery<Recipe> parseQuery = ParseQuery.getQuery(Recipe.class);
        parseQuery.whereContains(Recipe.KEY_TITLE, query);
        parseQuery.include(Recipe.KEY_IMAGE);
        parseQuery.include(Recipe.KEY_INGREDIENT_LIST);
        parseQuery.include(Recipe.KEY_AUTHOR);
        parseQuery.addAscendingOrder(Recipe.KEY_TITLE);
        parseQuery.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to populate recipes", e);
                    return;
                }
                Log.i(TAG, "Recipes uploaded: " + objects.toString());
                recipes = objects;
                if (recipes.size() == 0) {
                    showNoResultsDialog();
                }
                adapter.notifyDataSetChanged();
            }
        });
        params.put("Ingredients", currentUser.getIngredientsString());
        client.getRecipes(query, params, new JsonHttpResponseHandler() {

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
                    recipes.addAll(Recipe.getRecipes(jsonArray));
                    adapter.addAll(recipes);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });
    }

    private void getUser() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo(User.KEY_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.include(User.KEY_INGREDIENT_ARRAY);
        query.include(User.KEY_PROFILE_IMAGE);

        Log.i(TAG, "User id: " + ParseUser.getCurrentUser().getObjectId());
        try {
            ParseUser parseUser = query.get(ParseUser.getCurrentUser().getObjectId());
            currentUser = new User(parseUser);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to fetch user!", e);
        }
    }
}
