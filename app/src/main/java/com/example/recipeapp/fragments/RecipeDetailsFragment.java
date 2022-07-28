package com.example.recipeapp.fragments;

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
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.ReviewsAdapter;
import com.example.recipeapp.clients.RecipeClient;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.models.Comment;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.Review;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
    private final User currentUser = new User(ParseUser.getCurrentUser());
    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;
    @Nullable
    private RecipeClient client;
    @Nullable
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviews;

    public RecipeDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new RecipeClient(requireContext());
        reviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(requireContext(), reviews);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG, "Received bundle: " + recipe.getObjectId());
            findRecipe("None");
        }

    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentRecipeDetailsBinding.inflate(getLayoutInflater());
        binding.setFragmentRecipeDetailsController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showIngredients();
        setUpTabs();
        binding.tvRecipeName.setText(recipe.getTitle());
        binding.tvCookTime.setText(recipe.getCooktime() + " mins");
        binding.tvCuisine.setText(recipe.getCuisineType());
        String url = recipe.getImageUrl() == null ? recipe.getImage().getUrl() : recipe.getImageUrl();
        Glide.with(requireContext()).load(url).into(binding.ivImage);
        if (recipe.getRecipeId() != 0) {
            binding.tvUploadedBy.setVisibility(View.GONE);
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
            binding.tvInstructionsList.append((i + 1) + ". " + instructions.get(i) + "\n \n");
        }

        if (currentUser.isLikedbyCurrentUser(recipe)) {
            binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
        } else {
            binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        }

        binding.ibHeart.setOnClickListener(v -> findRecipe("like"));
        if (currentUser.isMadebyCurrentUser(recipe)) {
            binding.btnMade.setText("I Made it!");
        } else {
            binding.btnMade.setText("Make it!");
        }
        binding.btnMade.setOnClickListener(v -> findRecipe("made"));
        if (recipe.getRecipeId() == 0 && ParseUser.getCurrentUser().hasSameId(recipe.getAuthor().getParseUser())) {
            binding.ibEdit.setVisibility(View.VISIBLE);
        } else {
            binding.ibEdit.setVisibility(View.GONE);
        }
        Glide.with(requireContext()).load(currentUser.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReviews.setAdapter(reviewsAdapter);
        queryReviews();
    }

    private void setUpTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull final TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    showIngredients();
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    showInstructions();
                }
            }

            @Override
            public void onTabUnselected(@NonNull final TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(@NonNull final TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    showIngredients();
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    showInstructions();
                }
            }
        });
    }

    private void showInstructions() {
        binding.tvInstructionsList.setVisibility(View.VISIBLE);
        binding.tvIngredientList.setVisibility(View.GONE);
    }

    private void showIngredients() {
        binding.tvIngredientList.setVisibility(View.VISIBLE);
        binding.tvInstructionsList.setVisibility(View.GONE);
    }

    public void showShareAlert() {
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setMessage("Do you want to share this recipe?");
        alertDialogBuilder.setPositiveButton("Share", (dialog, which) -> {
            final Bundle bundle = new Bundle();
            bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
            NavHostFragment.findNavController(this).navigate(R.id.uploadPostFragment, bundle);
        });
        alertDialogBuilder.show();
    }

    public void postReview() {
        final String description = Objects.requireNonNull(binding.etReview.getText()).toString();
        final Review review = new Review();
        review.setAuthor(currentUser);
        review.setDescription(description);
        review.setRecipe(recipe);
        review.setRating(binding.rbRating.getRating());
        review.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error with saving review! " + e.getMessage());
                return;
            }
            Log.i(TAG, "Successfully added review");
            binding.etReview.setText("");
            binding.rbRating.setRating(0);
            binding.ivProfileImage.setImageResource(0);
            queryReviews();
        });
    }

    private void queryReviews() {
        final ParseQuery<Review> query = ParseQuery.getQuery("Review");
        query.whereEqualTo(Review.KEY_RECIPE, recipe);
        query.orderByDescending(Comment.KEY_CREATED_AT);
        query.include(Review.KEY_AUTHOR);
        query.include(Review.KEY_DESCRIPTION);
        query.include(Review.KEY_RATING);
        query.include(Review.KEY_RECIPE);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Error in fetching reviews");
                return;
            }
            Objects.requireNonNull(reviewsAdapter).clear();
            reviews = objects;
            reviewsAdapter.addAll(reviews);
            binding.tvReviewText.setText("Reviews(" + reviews.size() + ")");
        });
    }

    public void editRecipe(@NonNull View view) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
        view.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailsFragment_to_uploadPostFragment, bundle));
    }

    public void findRecipe(@NonNull String action) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_RECIPE_ID);
        query.include(Recipe.KEY_AUTHOR);
        query.whereEqualTo(Recipe.KEY_RECIPE_ID, recipe.getRecipeId());
        if (recipe.getRecipeId() == 0) {
            query.whereEqualTo(Recipe.KEY_OBJECT_ID, recipe.getObjectId());
        }
        query.findInBackground((objects, e) -> {
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
        });
    }

    private void madeRecipe() {
        if (currentUser.isMadebyCurrentUser(recipe)) {
            binding.btnMade.setText("Make it!");
        } else {
            binding.btnMade.setText("I Made it");
        }

        currentUser.madeRecipe(recipe);

        currentUser.getParseUser().saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error in setting recipe to made" + e);
                return;
            }
            Log.i(TAG, currentUser.getParseUser().getUsername() + " made recipe: " + recipe.getTitle());
        });
    }


    private void likeRecipe() {
        if (currentUser.isLikedbyCurrentUser(recipe)) {
            binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        } else {
            binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
        }
        currentUser.likeRecipe(recipe);

        currentUser.getParseUser().saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error in liking recipe" + e);
                return;
            }
            Log.i(TAG, currentUser.getParseUser().getUsername() + " liked recipe: " + recipe.getTitle());
        });
    }

    private void addRecipeToDatabase(@NonNull String action) {
        Log.i(TAG, "Adding recipe to database: " + recipe.getTitle());
        recipe.put("uploaded", true);
        recipe.saveInBackground(e -> {
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
        });
    }

    public void getIngredients() throws IOException {
        final List<String> ingredients = new ArrayList<>();

        Objects.requireNonNull(client).getRecipesDetailed(recipe.getRecipeId(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(final int statusCode, @NonNull final Headers headers, @NonNull final JSON json) {
                Log.i(TAG, "onSuccess! " + json);
                JSONArray jsonArray;
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
            public void onFailure(final int statusCode, @NonNull final Headers headers, final String response, @NonNull final Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);
            }
        });

        Log.i(TAG, "ingredients: " + ingredients);
    }

    public void goBack() {
        NavHostFragment.findNavController(this).navigateUp();
    }

}
