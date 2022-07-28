package com.example.recipeapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.LoginActivity;
import com.example.recipeapp.adapters.PostsAdapter;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.clients.ImageClient;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ProfileFragment";
    private final static int PICK_PHOTO_CODE = 1046;
    private static final int MIN_DISTANCE = 150;
    @NonNull
    private User CURRENT_USER = new User(ParseUser.getCurrentUser());
    @Nullable
    private File photoFile;
    private FragmentProfileBinding binding;
    @Nullable
    private RecipeSearchAdapter recipeSearchAdapter;
    @Nullable
    private PostsAdapter postsAdapter;
    private List<Recipe> recipes;
    private List<Post> posts;
    private ImageClient imageClient;
    private float x1, x2;

    public ProfileFragment() {
        // NO-OP
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        recipeSearchAdapter = new RecipeSearchAdapter(requireContext(), recipes);
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(requireContext(), posts);
        imageClient = new ImageClient(this);
        User.getUser(CURRENT_USER);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            CURRENT_USER = (User) bundle.get("User");
            Log.i(TAG, "Profile for " + CURRENT_USER.getParseUser().getUsername());
        }
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navigationDrawerView.setVisibility(View.GONE);
        binding.ibMenu.setVisibility(View.GONE);
        binding.tvUsername.setText("@" + CURRENT_USER.getParseUser().getUsername());
        binding.tvFullname.setText(CURRENT_USER.getFirstName() + " " + CURRENT_USER.getLastName());
        setProfileImage();
        binding.tvChangeProfileImage.setOnClickListener(v -> imageClient.onPickPhoto());
        binding.tvRecipeCount.setText(String.valueOf(CURRENT_USER.getRecipesUploaded().size()));

        binding.rvRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvRecipes.setAdapter(recipeSearchAdapter);

        binding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvPosts.setAdapter(postsAdapter);
        setUpTabs();
        setUpSwipe();
        Objects.requireNonNull(binding.tabLayout.getTabAt(0)).select();
        binding.logout.setOnClickListener(v -> showLogoutAlert());

        binding.navigationDrawerView.setNavigationItemSelectedListener(this);
        if (CURRENT_USER.getParseUser().hasSameId(ParseUser.getCurrentUser())) {
            binding.ibMenu.setVisibility(View.VISIBLE);
            binding.ibMenu.setOnClickListener(v -> binding.navigationDrawerView.setVisibility(View.VISIBLE));
        }
        binding.ibClose.setOnClickListener(v -> binding.navigationDrawerView.setVisibility(View.GONE));
        queryPosts();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpSwipe() {
        binding.rvRecipes.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    Objects.requireNonNull(binding.tabLayout.getTabAt(1)).select();
                }
            }
            return true;
        });

        binding.rvPosts.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    Objects.requireNonNull(binding.tabLayout.getTabAt(0)).select();
                }
            }
            return true;
        });
    }

    private void setUpTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull final TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    queryRecipes();
                    binding.rvRecipes.setVisibility(View.VISIBLE);
                    binding.rvPosts.setVisibility(View.GONE);
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    binding.rvRecipes.setVisibility(View.GONE);
                    binding.rvPosts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(@NonNull final TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(@NonNull final TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    queryRecipes();
                    binding.rvRecipes.setVisibility(View.VISIBLE);
                    binding.rvPosts.setVisibility(View.GONE);
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    binding.rvRecipes.setVisibility(View.GONE);
                    binding.rvPosts.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    private void queryPosts() {
        final ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_AUTHOR, CURRENT_USER.getParseUser());
        query.include(Post.KEY_TITLE);
        query.include(Post.KEY_AUTHOR);
        query.include(Post.KEY_IMAGE);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Unable to fetch posts", e);
                return;
            }
            Objects.requireNonNull(postsAdapter).clear();
            posts = objects;
            Log.i(TAG, "Successfully fetched posts" + posts.toString());
            postsAdapter.addAll(posts);
            binding.tvPostCount.setText(String.valueOf(posts.size()));
        });

    }

    private void queryRecipes() {
        Objects.requireNonNull(recipeSearchAdapter).clear();
        recipes = CURRENT_USER.getRecipesUploaded();
        Log.i(TAG, recipes.toString());
        recipeSearchAdapter.addAll(recipes);
    }

    private void changeProfileImage() {
        CURRENT_USER.setProfileImage(new ParseFile(Objects.requireNonNull(photoFile)));
        CURRENT_USER.getParseUser().saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with saving profile image!", e);
                Toast.makeText(getContext(), "Unable to save profile image. Please try again!", Toast.LENGTH_SHORT).show();
                return;
            }
            setProfileImage();
            Toast.makeText(getContext(), "Successfully saved profile image!", Toast.LENGTH_SHORT).show();

        });
    }

    private void setProfileImage() {
        Glide.with(requireContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoFile = imageClient.getPhotoFile();
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = imageClient.loadFromUri(photoUri);
            photoFile = imageClient.resizeFile(selectedImage);
            changeProfileImage();
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }

    public void showLogoutAlert() {
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setTitle("Logout from app?");
        alertDialogBuilder.setMessage("You will need to log back in to access the app!");
        alertDialogBuilder.setPositiveButton("Logout", (dialog, which) -> logoutUser());
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.show();
    }

    public void logoutUser() {
        Log.i(TAG, "Attempting to logout user!");
        ParseUser.logOut();
        final Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        final Bundle bundle = new Bundle();
        if (item.getItemId() == R.id.likedRecipes) {
            bundle.putString("MenuItem", "Liked Recipes");
        } else if (item.getItemId() == R.id.madeRecipes) {
            bundle.putString("MenuItem", "Made Recipes");
        } else if (item.getItemId() == R.id.likedPosts) {
            bundle.putString("MenuItem", "Liked Posts");
        }
        NavHostFragment.findNavController(this).navigate(R.id.profileMenuFragment, bundle);
        binding.navigationDrawerView.setVisibility(View.GONE);
        return true;
    }


}
