package com.example.recipeapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.recipeapp.activities.MainActivity;
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
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private File photoFile;
    private FragmentProfileBinding binding;
    @Nullable
    private RecipeSearchAdapter recipeSearchAdapter;
    @Nullable
    private PostsAdapter postsAdapter;
    private List<Recipe> recipes;
    private List<Post> posts;
    private ImageClient imageClient;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             @NonNull final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        recipeSearchAdapter = new RecipeSearchAdapter(getContext(), recipes);
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), posts);
        imageClient = new ImageClient(this);
        User.getUser(CURRENT_USER);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navigationDrawerView.setVisibility(View.GONE);
        binding.tvUsername.setText("@" + CURRENT_USER.getParseUser().getUsername());
        binding.tvFullname.setText(CURRENT_USER.getFirstName() + " " + CURRENT_USER.getLastName());
        setProfileImage();
        binding.tvChangeProfileImage.setOnClickListener(v -> imageClient.onPickPhoto());
        binding.tvRecipeCount.setText(String.valueOf(CURRENT_USER.getRecipesUploaded().size()));

        binding.rvRecipes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvRecipes.setAdapter(recipeSearchAdapter);

        binding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvPosts.setAdapter(postsAdapter);
        queryPosts();
        setUpTabs();
        Objects.requireNonNull(binding.tabLayout.getTabAt(0)).select();
        binding.logout.setOnClickListener(v -> showLogoutAlert());

        binding.navigationDrawerView.setNavigationItemSelectedListener(this);
    }

    private void setUpTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
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
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
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
            postsAdapter.clear();
            posts = objects;
            Log.i(TAG, "Successfully fetched posts" + posts.toString());
            postsAdapter.addAll(posts);
            binding.tvPostCount.setText(String.valueOf(posts.size()));
        });

    }

    private void queryRecipes() {
        recipeSearchAdapter.clear();
        recipes = CURRENT_USER.getRecipesUploaded();
        Log.i(TAG, recipes.toString());
        recipeSearchAdapter.addAll(recipes);
        return;
    }

    private void changeProfileImage() {
        CURRENT_USER.setProfileImage(new ParseFile(photoFile));
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

    private final void setProfileImage() {
        Glide.with(getContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        return;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
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

    @Override
    public void onPrepareOptionsMenu(@NonNull final Menu menu) {
        menu.findItem(R.id.navigation_drawer).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.navigation_drawer) {
            if (binding.navigationDrawerView.getVisibility() == View.VISIBLE)
                binding.navigationDrawerView.setVisibility(View.GONE);
            else
                binding.navigationDrawerView.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLogoutAlert() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle("Logout from app?");
        alertDialogBuilder.setMessage("You will need to log back in to access the app!");
        alertDialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    public void logoutUser() {
        Log.i(TAG, "Attempting to logout user!");
        ParseUser.logOut();
        final Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
