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
import com.example.recipeapp.databinding.FragmentUploadPostBinding;

public class UploadPostFragment extends Fragment {

    private static final String TAG = "FragmentUploadPost";
    private FragmentUploadPostBinding binding;

    public UploadPostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadPostBinding.inflate(getLayoutInflater());
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