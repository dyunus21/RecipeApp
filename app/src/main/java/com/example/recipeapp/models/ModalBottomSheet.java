package com.example.recipeapp.models;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentUploadBinding;
import com.example.recipeapp.databinding.ModalBottomSheetContentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    private ModalBottomSheetContentBinding binding;
    public static final String TAG = "Modal Bottom Sheet";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ModalBottomSheetContentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        this.dismiss();
    }

    public void goToUploadPost() {
        NavHostFragment.findNavController(this).navigate(R.id.uploadPostFragment);
        this.dismiss();
    }
}
