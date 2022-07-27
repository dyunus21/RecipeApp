package com.example.recipeapp.fragments;

import static android.app.Activity.RESULT_OK;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
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
import java.util.Objects;

import okhttp3.Headers;

public class RecipeSearchFragment extends Fragment {
    public static final String TAG = "RecipeSearchFragment";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    private final Map<String, String> params = new HashMap<>();
    private final User currentUser = new User(ParseUser.getCurrentUser());
    public List<Recipe> recipes;
    @Nullable
    protected RecipeSearchAdapter adapter;
    private FragmentRecipeSearchBinding binding;
    @Nullable
    private RecipeClient client;
    private ImageClient imageClient;
    @Nullable
    private File photoFile;


    // NO-OP
    public RecipeSearchFragment() {

    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(getLayoutInflater());
        binding.setFragmentRecipeSearchController(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new RecipeClient(requireContext());
        recipes = new ArrayList<>();
        adapter = new RecipeSearchAdapter(requireContext(), recipes);
        imageClient = new ImageClient(this);
        User.getUser(currentUser);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvRandomRecipes.setAdapter(adapter);
        binding.rvRandomRecipes.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecipes.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        setRefresh();
        binding.svSearch.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(@NonNull final SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(@NonNull final String query) {
                Log.i(TAG, query);
                try {
                    Objects.requireNonNull(adapter).clear();
                    binding.rvRecipes.setAdapter(adapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                    binding.rvRecipes.setLayoutManager(gridLayoutManager);
                    binding.rvRandomRecipes.setVisibility(View.GONE);
                    binding.rvRecipes.setVisibility(View.VISIBLE);
                    binding.textView.setVisibility(View.GONE);
                    binding.loadingAnimation.setVisibility(View.VISIBLE);
                    getRecipesByQuery(query);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Unable to search for recipe!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Unable to search for recipe!", e);
                }
            }
        });
    }

    private void setRefresh() {
        binding.rvRecipes.setVisibility(View.GONE);
        binding.swipeContainer.setOnRefreshListener(() -> {
            try {
                initializeScreen();
            } catch (IOException e) {
                Log.e(TAG, "Unable to initialize screen");
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
        final FilterDialogBinding filterDialogBinding = FilterDialogBinding.inflate(getLayoutInflater());
        final MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(requireContext());
        final ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cuisine));
        filterDialogBinding.actvCuisine.setAdapter(cuisineAdapter);

        for (String s : getResources().getStringArray(R.array.meal)) {
            final Chip chip = new Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle);
            chip.setText(s);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setFocusable(true);
            chip.setChipBackgroundColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_selector));
            chip.setId(ViewCompat.generateViewId());
            filterDialogBinding.cgMealType.addView(chip);
        }

        alertDialog.setPositiveButton("Set filter", (dialog, which) -> {
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
        }).setNegativeButton("Cancel", (dialog, which) -> Log.i(TAG, "Cancelled filter"));
        alertDialog.setView(filterDialogBinding.getRoot());
        alertDialog.show();
    }

    private void initializeScreen() throws IOException {
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        Objects.requireNonNull(client).getRandomRecipes(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, @NonNull final Headers headers, @NonNull final JSON json) {
                Log.i(TAG, "Successfully initialized search screen " + json);
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("recipes");
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
                try {
                    Objects.requireNonNull(adapter).clear();
                    recipes = Recipe.getRecipes(Objects.requireNonNull(jsonArray));
                    adapter.addAll(recipes);
                    binding.rvRandomRecipes.scrollToPosition(0);
                    binding.swipeContainer.setRefreshing(false);
                    binding.loadingAnimation.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(final int statusCode, @NonNull final Headers headers, final String response, final @NonNull Throwable throwable) {
                Log.e(TAG, "Unable to fetch random recipes" + throwable);
            }
        });
    }

    private void showNoResultsDialog() {
        final MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        materialAlertDialogBuilder.setMessage("No results found!");
        materialAlertDialogBuilder.show();
    }

    public void getRecipesByQuery(final String query) throws IOException {

        final ParseQuery<Recipe> parseQuery = ParseQuery.getQuery(Recipe.class);
        parseQuery.whereContains(Recipe.KEY_TITLE, query);
        parseQuery.include(Recipe.KEY_IMAGE);
        parseQuery.include(Recipe.KEY_INGREDIENT_LIST);
        parseQuery.include(Recipe.KEY_AUTHOR);
        parseQuery.addAscendingOrder(Recipe.KEY_TITLE);
        parseQuery.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Unable to populate recipes", e);
                return;
            }
            Log.i(TAG, "Recipes uploaded: " + objects.toString());
            recipes = objects;
            Objects.requireNonNull(adapter).notifyDataSetChanged();
        });
        params.put("Ingredients", currentUser.getIngredientsString());
        Objects.requireNonNull(client).getRecipes(query, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(final int statusCode, @NonNull final Headers headers, @NonNull final JSON json) {
                Log.i(TAG, "onSuccess! " + json);
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("results");
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
                try {
                    recipes.addAll(Recipe.getRecipes(Objects.requireNonNull(jsonArray)));
                    binding.loadingAnimation.setVisibility(View.GONE);
                    if (recipes.size() == 0) {
                        showNoResultsDialog();
                    }
                    Objects.requireNonNull(adapter).addAll(recipes);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }
            }

            @Override
            public void onFailure(final int statusCode, @NonNull final Headers headers, final String response, @NonNull final Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });
    }

    public void initCamera() {
        imageClient.launchCamera();
    }

    public void showPreview() {
        Log.i(TAG, "Show preview");
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Preview");
        final ImageSearchDialogBinding imageSearchDialogBinding = ImageSearchDialogBinding.inflate(getLayoutInflater());
        Glide.with(requireContext()).load(photoFile).into(imageSearchDialogBinding.ivPreview);
        alertDialogBuilder.setPositiveButton("Search", (dialog, which) -> {
            Log.i(TAG, "Search for recipes based on image");
            try {
                binding.loadingAnimation.setVisibility(View.VISIBLE);
                searchRecipesByImage();
            } catch (IOException e) {
                Log.e(TAG, "Unable to search for recipes based on image", e);
            }
        });
        alertDialogBuilder.setView(imageSearchDialogBinding.getRoot());
        alertDialogBuilder.show();
    }

    private void searchRecipesByImage() throws IOException {
        Objects.requireNonNull(client).getRecipesByImage(Objects.requireNonNull(photoFile), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, @NonNull final Headers headers, @NonNull final JSON json) {
                Log.i(TAG, "On success!" + json);
                JSONArray jsonArray;
                try {
                    jsonArray = json.jsonObject.getJSONArray("recipes");
                    createRecipeIdArray(jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to fetch recipes from image!", e);
                }

            }

            @Override
            public void onFailure(final int statusCode, @NonNull final Headers headers, final String response, @NonNull final Throwable throwable) {
                Log.e(TAG, "onFailure " + throwable + " " + response);
            }
        });
    }

    private void createRecipeIdArray(@NonNull final JSONArray jsonArray) throws JSONException {
        final List<String> recipeIds = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipeIds.add(String.valueOf(jsonArray.getJSONObject(i).getInt("id")));
        }
        final String ids = String.join(",", recipeIds);
        Log.i(TAG, "recipeId Array: " + ids);
        getRecipesById(ids);
    }

    private void getRecipesById(final String ids) {
        Objects.requireNonNull(client).getRecipeInformationBulk(ids, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, @NonNull final Headers headers, @NonNull final JSON json) {
                Log.i(TAG, "Successfully Received array of recipe information based on ids" + json);
                final JSONArray jsonArray = json.jsonArray;
                try {
                    recipes = Recipe.getRecipes(jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to getRecipes from jsonArray", e);
                }
                if (recipes.size() == 0) {
                    showNoResultsDialog();
                }
                Objects.requireNonNull(adapter).clear();
                binding.rvRecipes.setVisibility(View.VISIBLE);
                binding.rvRandomRecipes.setVisibility(View.GONE);
                binding.textView.setVisibility(View.GONE);
                adapter.addAll(recipes);
                binding.loadingAnimation.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(final int statusCode, @NonNull final Headers headers, final String response, @NonNull final Throwable throwable) {
                Log.e(TAG, "Unable to fetch recipe information by ids" + response);

            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        Log.i(TAG, "Reached here!!!");
        photoFile = imageClient.getPhotoFile();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "onActivity result camera");
            if (resultCode == RESULT_OK) {
                final Bitmap takenImage = BitmapFactory.decodeFile(Objects.requireNonNull(photoFile).getAbsolutePath());
                photoFile = imageClient.resizeFile(takenImage);
                Log.i(TAG, "File: " + photoFile);
                showPreview();
            } else {
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            final Uri photoUri = data.getData();
            final Bitmap selectedImage = imageClient.loadFromUri(photoUri);
            photoFile = imageClient.resizeFile(selectedImage);
            Log.i(TAG, "File: " + photoFile);
        }
    }
}
