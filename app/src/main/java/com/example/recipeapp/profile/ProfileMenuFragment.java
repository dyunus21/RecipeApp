package com.example.recipeapp.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.recipeapp.socialFeed.adapters.PostsAdapter;
import com.example.recipeapp.recipeSearch.adapters.RecipeSearchAdapter;
import com.example.recipeapp.databinding.FragmentProfileMenuBinding;
import com.example.recipeapp.models.parse.Post;
import com.example.recipeapp.models.parse.Recipe;
import com.example.recipeapp.models.parse.User;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileMenuFragment extends Fragment {

    public static final String TAG = "ProfileMenuFragment";
    @NonNull
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private FragmentProfileMenuBinding binding;
    private List<Recipe> recipeList;
    private List<Post> postList;
    private RecipeSearchAdapter recipeSearchAdapter;
    private PostsAdapter postsAdapter;
    private String menuItem;

    public ProfileMenuFragment() {
        // NO-OP
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeList = new ArrayList<>();
        recipeSearchAdapter = new RecipeSearchAdapter(requireContext(), recipeList);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(requireContext(), postList);
        User.getUser(CURRENT_USER);
        setHasOptionsMenu(true);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            menuItem = bundle.getString("MenuItem");
            Log.i(TAG, "Received bundle: " + menuItem);
        }
    }

    private void setUpContent() {
        switch (menuItem) {
            case "Liked Recipes":
                getRecipesLiked();
                break;
            case "Made Recipes":
                getRecipesMade();
                break;
            case "Liked Posts":
                getPostsLiked();
                break;
        }
    }

    @NonNull
    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentProfileMenuBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragmentProfileMenuController(this);
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

    private void getPostsLiked() {
        final ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_LIKED_BY);
        query.include(Post.KEY_AUTHOR);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_TITLE);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Unable to fetch posts", e);
                return;
            }
            final List<Post> posts = new ArrayList<>();
            for (Post post : objects) {
                if (post.isLikedbyCurrentUser(CURRENT_USER.getParseUser())) {
                    posts.add(post);
                    Log.i(TAG, "Added " + post.getTitle());
                }
            }
            postsAdapter.clear();
            postList = posts;
            postsAdapter.addAll(postList);
            binding.rvPosts.setVisibility(View.VISIBLE);
            binding.rvRecipes.setVisibility(View.GONE);
            Log.i(TAG, "Liked posts: " + postList.toString());
        });
    }

    public void goToProfile() {
        NavHostFragment.findNavController(this).navigateUp();
    }
}