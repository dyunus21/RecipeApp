package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeClient;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.models.Recipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;
    private RecipeClient client;

    public RecipeDetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeDetailsBinding.inflate(getLayoutInflater());
        client = new RecipeClient(getContext());
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG, "Received bundle: " + recipe.getTitle());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvRecipeName.setText(recipe.getTitle());
        binding.tvCookTime.setText("Cooktime: " + recipe.getCooktime() + " mins");
        binding.tvCuisine.setText("Cuisine: " + recipe.getCuisineType());
        if (recipe.getImageUrl() != null) {
            Glide.with(getContext()).load(recipe.getImageUrl()).into(binding.ivImage);
        } else {
            Glide.with(getContext()).load(recipe.getImage().getUrl()).into(binding.ivImage);
        }
        if (recipe.getAuthor() == null) {
            try {
                getIngredients();
                Log.i(TAG, "list: " + recipe.getIngredientList().toString());

            } catch (IOException e) {
                Log.e(TAG, "Error with getting ingredients", e);
            }
        } else {
            List<String> ingredients = recipe.getIngredientList();
            for (int i = 0; i < ingredients.size(); i++) {
                binding.tvIngredientList.append("• " + ingredients.get(i) + "\n");
            }
        }

        List<String> instructions = recipe.getInstructions();
        Log.i(TAG, "instructions: " + instructions.toString());
        for (int i = 0; i < instructions.size(); i++) {
            binding.tvInstructionsList.append((i + 1) + ". " + instructions.get(i) + "\n");
        }

        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToSearch(v);
            }
        });

        // TODO: Set up like and I made this button

    }

    public void getIngredients() throws IOException {
        List<String> ingredients = new ArrayList<>();

        client.getRecipesDetailed(recipe.getRecipeId(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = json.jsonObject.getJSONArray("extendedIngredients");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ingredients.add(i, jsonArray.getJSONObject(i).getString("original"));
                    }
                    Log.i(TAG, "Saved ingredients " + recipe.getIngredientList().toString());
                    for (int i = 0; i < ingredients.size(); i++) {
                        binding.tvIngredientList.append("• " + ingredients.get(i) + "\n");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });

        Log.i(TAG, "ingredients: " + ingredients);
    }


    public void goBackToSearch(View view) {
        NavHostFragment.findNavController(this).navigate(R.id.recipeSearchFragment);
    }


}