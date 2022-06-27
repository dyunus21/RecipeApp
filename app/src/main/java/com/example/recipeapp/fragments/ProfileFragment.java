package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipeapp.adapters.ProfileAdapter;
import com.example.recipeapp.adapters.RecipeSearchAdapter;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.models.ImageClient;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private static final int PROFILE_PHOTO_CODE = 0;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser()); // TODO: FIX THIS
    private ProfileAdapter adapter;
    private List<Recipe> recipes;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvUsername.setText(CURRENT_USER.getParseUser().getUsername());
        binding.tvFullname.setText(CURRENT_USER.getFirstName() + " " + CURRENT_USER.getLastName());
        Glide.with(getContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        binding.tvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageClient imageClient = new ImageClient(getContext());
                imageClient.onPickPhoto(view, PROFILE_PHOTO_CODE);
                Glide.with(getContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
            }
        });
        recipes = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(),recipes);
        binding.rvUploadedRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUploadedRecipes.setAdapter(adapter);
        queryRecipes("uploaded");
    }

    private void queryRecipes(String type) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        if(type.equals("uploaded")) {
            query.whereEqualTo(Recipe.KEY_AUTHOR, CURRENT_USER.getParseUser());
        }
        query.include(Recipe.KEY_IMAGE_URL);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting uploaded recipes",e);
                    return;
                }
                adapter.clear();
                recipes.addAll(objects);
                adapter.notifyDataSetChanged();
                binding.tvRecipeCount.setText(String.valueOf(recipes.size()));
            }
        });
    }
}