package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.recipeapp.adapters.PostsAdapter;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.databinding.FragmentProfileMenuBinding;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileMenuFragment extends Fragment {

    public static final String TAG = "ProfileMenuFragment";
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private FragmentProfileMenuBinding binding;
    private List<Recipe> recipeList;
    private List<Post> postList;
    private RecipeSearchAdapter recipeSearchAdapter;
    private PostsAdapter postsAdapter;
    private String menuItem;

    public ProfileMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeList = new ArrayList<>();
        recipeSearchAdapter = new RecipeSearchAdapter(getContext(), recipeList);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), postList);
        User.getUser(CURRENT_USER);
        setHasOptionsMenu(true);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            menuItem = bundle.getString("MenuItem");
            Log.i(TAG, "Received bundle: " + menuItem);
        }
    }

    private void setUpContent() {
        if (menuItem.equals("Liked Recipes")) {
            getRecipesLiked();
        } else if (menuItem.equals("Made Recipes")) {
            getRecipesMade();
        } else if (menuItem.equals("Liked Posts")) {
            //getPostsLiked();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileMenuBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvRecipes.setAdapter(recipeSearchAdapter);
        binding.rvRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvPosts.setAdapter(postsAdapter);
        binding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.tvPageTitle.setText(menuItem);
        setUpContent();
    }

    private void getRecipesLiked() {
        recipeSearchAdapter.clear();
        recipeList = CURRENT_USER.getRecipesLiked();
        recipeSearchAdapter.addAll(recipeList);
        binding.rvRecipes.setVisibility(View.VISIBLE);
        binding.rvPosts.setVisibility(View.GONE);
    }

    private void getRecipesMade() {
        recipeSearchAdapter.clear();
        recipeList = CURRENT_USER.getRecipesMade();
        recipeSearchAdapter.addAll(recipeList);
        binding.rvRecipes.setVisibility(View.VISIBLE);
        binding.rvPosts.setVisibility(View.GONE);
    }

}