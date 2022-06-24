package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.models.ImageClient;
import com.example.recipeapp.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private static final int PROFILE_PHOTO_CODE = 0;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser()); // TODO: FIX THIS

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
    }
}