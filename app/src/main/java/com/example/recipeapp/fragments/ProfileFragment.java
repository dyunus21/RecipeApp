package com.example.recipeapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.ProfileAdapter;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.models.ImageClient;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private final static int PICK_PHOTO_CODE = 1046;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private File photoFile;
    private FragmentProfileBinding binding;
    private ProfileAdapter adapter;
    private List<Recipe> recipes;
    private ImageClient imageClient;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), recipes);
        imageClient = new ImageClient(this);
        User.getUser(CURRENT_USER);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvUsername.setText("@" + CURRENT_USER.getParseUser().getUsername());
        binding.tvFullname.setText(CURRENT_USER.getFirstName() + " " + CURRENT_USER.getLastName());
        setProfileImage();
        binding.tvChangeProfileImage.setOnClickListener(v -> imageClient.onPickPhoto(v));
        binding.tvRecipeCount.setText(String.valueOf(CURRENT_USER.getRecipesUploaded().size()));
        binding.tvMadeCount.setText(String.valueOf(CURRENT_USER.getRecipesMade().size()));
        binding.rvUploadedRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUploadedRecipes.setAdapter(adapter);
        setUpTabs();

    }

    private void setUpTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    queryRecipes("uploaded");
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    queryRecipes("liked");
                } else if (tab == binding.tabLayout.getTabAt(2)) {
                    queryRecipes("made");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab == binding.tabLayout.getTabAt(0)) {
                    queryRecipes("uploaded");
                } else if (tab == binding.tabLayout.getTabAt(1)) {
                    queryRecipes("liked");
                } else if (tab == binding.tabLayout.getTabAt(2)) {
                    queryRecipes("made");
                }
            }
        });
    }

    private void queryRecipes(String type) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        adapter.clear();
        if (type.equals("uploaded")) {
            recipes = CURRENT_USER.getRecipesUploaded();
        } else if (type.equals("liked")) {
            recipes = CURRENT_USER.getRecipesLiked();
        } else if (type.equals("made")) {
            recipes = CURRENT_USER.getRecipesMade();
        }
        Log.i(TAG, recipes.toString());
        adapter.addAll(recipes);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.logout).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }
}
