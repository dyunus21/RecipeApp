package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.models.Recipe;

import java.util.List;

public class RecipeDetailsFragment extends Fragment {
    private static final String TAG = "RecipeDetailsFragment";
    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;

    public RecipeDetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeDetailsBinding.inflate(getLayoutInflater());
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG,"Received bundle: " + recipe.getTitle());
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvRecipeName.setText(recipe.getTitle());
        binding.tvCookTime.setText("Cooktime: " + recipe.getCooktime() + " mins");
        binding.tvCuisine.setText("Cuisine: " + recipe.getCuisineType());
        Glide.with(getContext()).load(recipe.getImageUrl()).into(binding.ivImage);

        List<String> instructions = recipe.getInstructions();
        Log.i(TAG,"instructions: " + instructions.toString());
        for(int i = 0; i<instructions.size(); i++) {
            binding.tvInstructionsList.append((i+1) + ". " + instructions.get(i) + "\n \n");
        }
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToSearch(v);
            }
        });

        // TODO: Setup ingredient list
        // TODO: Set up like and I made this button

    }


    public void goBackToSearch(View view) {
        NavHostFragment.findNavController(this).navigate(R.id.recipeSearchFragment);
    }
}