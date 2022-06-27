package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.databinding.FragmentUploadRecipeBinding;

public class UploadRecipeFragment extends Fragment {
    private FragmentUploadRecipeBinding binding;
    private static final String TAG = "FragmentUploadRecipe";
    public UploadRecipeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadRecipeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToUpload();
            }
        });
    }

    private void goBackToUpload() {
        NavHostFragment.findNavController(this).navigate(R.id.uploadFragment);
    }
}