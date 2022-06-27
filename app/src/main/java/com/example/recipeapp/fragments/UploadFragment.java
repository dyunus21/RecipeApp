package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentUploadBinding;

public class UploadFragment extends Fragment {

    private static final String TAG = "UploadFragment";
    private FragmentUploadBinding binding;

    public UploadFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadPost();
            }
        });
        binding.btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadRecipe();
            }
        });
    }

    public void goToUploadRecipe() {
        NavHostFragment.findNavController(this).navigate(R.id.uploadRecipeFragment);
    }

    public void goToUploadPost() {
        NavHostFragment.findNavController(this).navigate(R.id.uploadPostFragment);
    }
}