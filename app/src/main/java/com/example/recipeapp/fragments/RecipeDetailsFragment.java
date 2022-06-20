package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.recipeapp.R;

public class RecipeDetailsFragment extends Fragment {


    public RecipeDetailsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }
}