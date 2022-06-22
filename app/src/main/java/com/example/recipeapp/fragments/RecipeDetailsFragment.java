package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.models.Recipe;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
//    private FragmentRecipeDetailsBinding binding;
    private int recipeId;

    public RecipeDetailsFragment() {

    }
    public RecipeDetailsFragment(int recipeId) {
        this.recipeId = recipeId;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }
}