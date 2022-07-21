package com.example.recipeapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeClient;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.ReviewsAdapter;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.models.Comment;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.Review;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

import okhttp3.Headers;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
    private final boolean recipeInDatabase = false;
    private final User currentUser = new User(ParseUser.getCurrentUser());
    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;
    private RecipeClient client;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviews;

    public RecipeDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new RecipeClient(getContext());
        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(getContext(), reviews);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG, "Received bundle: " + recipe.getObjectId());
            findRecipe("None");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Recipe Details: " + recipe.getTitle());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvRecipeName.setText(recipe.getTitle());
        binding.tvCookTime.setText("Cooktime: " + recipe.getCooktime() + " mins");
        binding.tvCuisine.setText("Cuisine: " + recipe.getCuisineType());
        String url = recipe.getImageUrl() == null ? recipe.getImage().getUrl() : recipe.getImageUrl();
        Glide.with(getContext()).load(url).into(binding.ivImage);
        if (recipe.getRecipeId() != 0) {
            binding.tvUploadedBy.setVisibility(View.GONE);
            try {
                getIngredients();
                Log.i(TAG, "list: " + recipe.getIngredientList().toString());

            } catch (IOException e) {
                Log.e(TAG, "Error with getting ingredients", e);
            }
        } else {
//            binding.tvUploadedBy.setText("Uploaded by: @" + recipe.getAuthor().getParseUser().getUsername());
            List<String> ingredients = recipe.getIngredientList();
            Log.i(TAG, "Ingredients: " + ingredients.toString());
            for (int i = 0; i < ingredients.size(); i++) {
                binding.tvIngredientList.append("• " + ingredients.get(i) + "\n");
            }
        }

        List<String> instructions = recipe.getInstructions();
        Log.i(TAG, "instructions: " + instructions.toString());
        for (int i = 0; i < instructions.size(); i++) {
            binding.tvInstructionsList.append((i + 1) + ". " + instructions.get(i) + "\n \n");
        }

        if (recipe.getRecipeId() != 0) {
            binding.tvUploadedBy.setText("");
        } else {
            binding.tvUploadedBy.setText("Uploaded by: @username");
        }

        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
        if (recipe.getRecipeId() == 0 && ParseUser.getCurrentUser().hasSameId(recipe.getAuthor().getParseUser())) {
            binding.ibEdit.setVisibility(View.VISIBLE);
        } else {
            binding.ibEdit.setVisibility(View.GONE);
        }
        Glide.with(getContext()).load(currentUser.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReviews.setAdapter(reviewsAdapter);
        queryReviews();
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview();
            }
        });

        binding.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                alertDialogBuilder.setMessage("Do you want to share this recipe?");
                alertDialogBuilder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Bundle bundle = new Bundle();
                        bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
                        UploadPostFragment uploadPostFragment = new UploadPostFragment();
                        uploadPostFragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_host_fragment, uploadPostFragment)
                                .commit();
                    }
                });
                alertDialogBuilder.show();
            }
        });

    }

    private void postReview() {
        String description = binding.etReview.getText().toString();
        Review review = new Review();
        review.setAuthor(currentUser);
        review.setDescription(description);
        review.setRecipe(recipe);
        review.setRating(binding.rbRating.getRating());
        review.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with saving review! " + e.getMessage());
                    return;
                }
                Log.i(TAG, "Successfully added review");
                binding.etReview.setText("");
                binding.rbRating.setRating(0);
                binding.ivProfileImage.setImageResource(0);
                queryReviews();
            }
        });
    }

    private void queryReviews() {
        ParseQuery<Review> query = ParseQuery.getQuery("Review");
        query.whereEqualTo(Review.KEY_RECIPE, recipe);
        query.orderByDescending(Comment.KEY_CREATED_AT);
        query.include(Review.KEY_AUTHOR);
        query.include(Review.KEY_DESCRIPTION);
        query.include(Review.KEY_RATING);
        query.include(Review.KEY_RECIPE);
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in fetching reviews");
                    return;
                }
                reviewsAdapter.clear();
                reviews = objects;
                reviewsAdapter.addAll(reviews);
                binding.tvReviewText.setText("Reviews(" + reviews.size() + ")");
            }
        });
    }

    public void editRecipe(View view) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
        view.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailsFragment_to_uploadPostFragment, bundle));
    }

    public void findRecipe(String action) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_RECIPE_ID);
        query.include(Recipe.KEY_AUTHOR);
        query.whereEqualTo(Recipe.KEY_RECIPE_ID, recipe.getRecipeId());
        if (recipe.getRecipeId() == 0) {
            query.whereEqualTo(Recipe.KEY_OBJECT_ID, recipe.getObjectId());
        }
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
                } else if (action.equals("made")) {
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
                    Log.i(TAG, "Saved ingredients " + recipe.getIngredientList().toString());
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


    public void goBackToSearch() {
        NavHostFragment.findNavController(this).navigate(R.id.recipeSearchFragment);
    }
}
