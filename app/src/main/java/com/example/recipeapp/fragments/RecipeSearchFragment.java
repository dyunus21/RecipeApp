package com.example.recipeapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.clients.ImageClient;
import com.example.recipeapp.clients.RecipeClient;
import com.example.recipeapp.databinding.FilterDialogBinding;
import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;
import com.example.recipeapp.databinding.ImageSearchDialogBinding;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class RecipeSearchFragment extends Fragment {
    public static final String TAG = "RecipeSearchFragment";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    private final Map<String, String> params = new HashMap<>();
    private final User currentUser = new User(ParseUser.getCurrentUser());
    public List<Recipe> recipes;
    protected RecipeSearchAdapter adapter;
    private FragmentRecipeSearchBinding binding;
    private RecipeClient client;
    private ImageClient imageClient;
    private File photoFile;
    private ProgressDialog progressDialog;  //Replace with shimmer effect


    public RecipeSearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(getLayoutInflater());
        binding.setFragmentRecipeSearchController(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new RecipeClient(getContext());
        recipes = new ArrayList<>();
        adapter = new RecipeSearchAdapter(getContext(), recipes);
        imageClient = new ImageClient(this);
        progressDialog = new ProgressDialog(getContext());
        User.getUser(currentUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        binding.rvRandomRecipes.setAdapter(adapter);
        binding.rvRandomRecipes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // Commented to limit API calls during testing
        // setRefresh();

        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, query);
                try {
                    adapter.clear();
                    binding.rvRecipes.setAdapter(adapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    binding.rvRecipes.setLayoutManager(gridLayoutManager);
                    binding.rvRandomRecipes.setVisibility(View.GONE);
                    binding.textView.setVisibility(View.GONE);
                    getRecipesByQuery(query);
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

    private void setRefresh() {
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
    }

    public void initFilterDialog() {
        FilterDialogBinding filterDialogBinding = FilterDialogBinding.inflate(getLayoutInflater());
        final MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(getContext());
        final ArrayAdapter cuisineAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cuisine));
        filterDialogBinding.actvCuisine.setAdapter(cuisineAdapter);

        for (String s : getResources().getStringArray(R.array.meal)) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
            chip.setText(s);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setFocusable(true);
            chip.setChipBackgroundColor(ContextCompat.getColorStateList(getContext(), R.color.chip_selector));
            chip.setId(ViewCompat.generateViewId());
            filterDialogBinding.cgMealType.addView(chip);
        }

        alertDialog.setPositiveButton("Set filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                params.put("Cooktime", filterDialogBinding.etCooktime.getText().toString());
                params.put("Cuisine", filterDialogBinding.actvCuisine.getText().toString());
                String mealType = "";
                List<Integer> checkedChipIds = filterDialogBinding.cgMealType.getCheckedChipIds();
                if (!checkedChipIds.isEmpty()) {
                    Chip chip = ((Chip) (filterDialogBinding.cgMealType.findViewById(checkedChipIds.get(0))));
                    mealType = chip.getText().toString();
                    Log.i(TAG, "Meal type selected! " + mealType);
                }
                params.put("MealType", mealType);
                params.put("switchIngredients", String.valueOf(filterDialogBinding.switchIngredients.isChecked()));
                Log.i(TAG, params.toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Cancelled filter");
            }
        });
        alertDialog.setView(filterDialogBinding.getRoot());
        alertDialog.show();
    }

    private void initializeScreen() throws IOException {
        progressDialog.setMessage("Fetching recipes...");
        progressDialog.show();
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
                    binding.rvRandomRecipes.scrollToPosition(0);
                    binding.swipeContainer.setRefreshing(false);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Unable to fetch random recipes" + throwable.toString());
            }
        });
    }

    private void showNoResultsDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setMessage("No results found!");
        materialAlertDialogBuilder.show();
    }

    public void getRecipesByQuery(String query) throws IOException {

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
                    if (recipes.size() == 0) {
                        showNoResultsDialog();
                    }
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

    public void initCamera() {
        imageClient.launchCamera();
    }

    public void showPreview() {
        Log.i(TAG, "Show preview");
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Preview");
        final ImageSearchDialogBinding imageSearchDialogBinding = ImageSearchDialogBinding.inflate(getLayoutInflater());
        Glide.with(getContext()).load(photoFile).into(imageSearchDialogBinding.ivPreview);
        alertDialogBuilder.setPositiveButton("Search", (dialog, which) -> {
            Log.i(TAG, "Search for recipes based on image");
            try {
                progressDialog.setMessage("Fetching recipes...");
                progressDialog.show();
                searchRecipesByImage();
            } catch (IOException e) {
                Log.e(TAG, "Unable to search for recipes based on image", e);
            }
        });
        alertDialogBuilder.setView(imageSearchDialogBinding.getRoot());
        alertDialogBuilder.show();
    }

    private void searchRecipesByImage() throws IOException {
        client.getRecipesByImage(photoFile, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "On sucessss!" + json.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("recipes");
                    createRecipeIdArray(jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to fetch recipes from image!", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + throwable.toString() + " " + response);
            }
        });
    }

    private void createRecipeIdArray(JSONArray jsonArray) throws JSONException {
        List<String> recipeIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipeIds.add(String.valueOf(jsonArray.getJSONObject(i).getInt("id")));
        }
        String ids = String.join(",", recipeIds);
        Log.i(TAG, "recipeId Array: " + ids);
        getRecipesById(ids);
    }

    private void getRecipesById(String ids) {
        client.getRecipeInformationBulk(ids, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Successfully Received array of recipe information based on ids" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    recipes.addAll(Recipe.getRecipes(jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to getRecipes from jsonArray", e);
                }
                if (recipes.size() == 0) {
                    showNoResultsDialog();
                }
                adapter.addAll(recipes);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Unable to fetch recipe information by ids" + response);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG, "Reached here!!!");
        photoFile = imageClient.getPhotoFile();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "onActivity result camera");
            if (resultCode == RESULT_OK) {
                final Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                photoFile = imageClient.resizeFile(takenImage);
                Log.i(TAG, "File: " + photoFile.toString());
                showPreview();
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            final Uri photoUri = data.getData();
            Bitmap selectedImage = imageClient.loadFromUri(photoUri);
            photoFile = imageClient.resizeFile(selectedImage);
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }
}
