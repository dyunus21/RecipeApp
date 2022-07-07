package com.example.recipeapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.recipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;
    private final boolean recipeInDatabase = false;
    private RecipeClient client;
    private final User currentUser = new User(ParseUser.getCurrentUser());

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
            Log.i(TAG, "Received bundle: " + recipe.getRecipeId());
            findRecipe("None");
            User.getUser(recipe.getAuthor());
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
        if (recipe.getRecipeId() != 0) {
            try {
                getIngredients();
                Log.i(TAG, "list: " + recipe.getIngredientList().toString());

            } catch (IOException e) {
                Log.e(TAG, "Error with getting ingredients", e);
            }
        } else {
            List<String> ingredients = recipe.getIngredientList();
            Log.i(TAG, "Ingredients: " + ingredients.toString());
            for (int i = 0; i < ingredients.size(); i++) {
                binding.tvIngredientList.append("• " + ingredients.get(i) + "\n");
            }
        }

        List<String> instructions = recipe.getInstructions();
        Log.i(TAG, "instructions: " + instructions.toString());
        for (int i = 0; i < instructions.size(); i++) {
            binding.tvInstructionsList.append((i + 1) + ". " + instructions.get(i) + "\n");
        }

        if (recipe.getRecipeId() != 0) {
            binding.tvUploadedBy.setText("");
        } else {
            binding.tvUploadedBy.setText("Uploaded by: @username");
        }

        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToSearch(v);
            }
        });

        // TODO: button does not change color to indicate liked recipes because ids differ each session
        if (currentUser.isLikedbyCurrentUser(recipe)) {
            binding.ibHeart.setBackgroundResource(R.drawable.heart_filled);
        } else {
            binding.ibHeart.setBackgroundResource(R.drawable.heart);
        }
        binding.ibHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findRecipe("like");
            }
        });

        if (currentUser.isMadebyCurrentUser(recipe)) {
            // TODO Change Image button color to gray
            binding.btnMade.setText("I Made it!");
        } else {
            binding.btnMade.setText("Make it!");
        }
        binding.btnMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findRecipe("made");
            }
        });
    }

    private void findRecipe(String action) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_RECIPE_ID);
        query.include(Recipe.KEY_AUTHOR);
        query.whereEqualTo(Recipe.KEY_RECIPE_ID, recipe.getRecipeId());
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in finding recipe!");
                    return;
                }
                Log.i(TAG, "Recipes found: " + objects.toString());
                if (objects.size() == 0) {
                    addRecipeToDatabase(action);
                } else if (action.equals("like")) {
                    likeRecipe();
                } else if(action.equals("made")){
                    madeRecipe();
                } else {
                    recipe = objects.get(0);
                    Log.i(TAG, "Recipe: " + recipe.getTitle());
                }
            }
        });
    }

    private void madeRecipe() {
        if (currentUser.isMadebyCurrentUser(recipe)) {
            // TODO Change Image button color to gray
            binding.btnMade.setText("Make it!");
        } else {
            binding.btnMade.setText("I Made it");
        }

        currentUser.madeRecipe(recipe);

        currentUser.getParseUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in setting recipe to made" + e);
                    return;
                }
                Log.i(TAG, currentUser.getParseUser().getUsername() + " made recipe: " + recipe.getTitle());
            }
        });
    }


    private void likeRecipe() {
        if (currentUser.isLikedbyCurrentUser(recipe)) {
            binding.ibHeart.setBackgroundResource(R.drawable.heart);
        } else {
            binding.ibHeart.setBackgroundResource(R.drawable.heart_filled);
        }
        currentUser.likeRecipe(recipe);

        currentUser.getParseUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in liking recipe" + e);
                    return;
                }
                Log.i(TAG, currentUser.getParseUser().getUsername() + " liked recipe: " + recipe.getTitle());
            }
        });
//        binding.tvLikes.setText(post.getLikeCount());
    }

    private void addRecipeToDatabase(String action) {
        Log.i(TAG, "Adding recipe to database: " + recipe.getTitle());
        recipe.put("uploaded", true);
        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in saving current recipe");
                    return;
                }
                Log.i(TAG, "New Recipe saved in database!");
                if (action.equals("like")) {
                    likeRecipe();
                } else {
                    madeRecipe();
                }
            }
        });
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
                    recipe.setIngredientList(ingredients);
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