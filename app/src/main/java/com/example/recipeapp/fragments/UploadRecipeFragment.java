package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding;
import com.example.recipeapp.databinding.FragmentUploadRecipeBinding;
import com.example.recipeapp.models.Recipe;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

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
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRecipe();
            }
        });
    }

    private void validateRecipe() {
        String title = binding.etRecipeName.getText().toString();
        String cuisineType = binding.etCuisine.getText().toString();
        int cooktime = Integer.parseInt(binding.etCooktime.getText().toString());  // TODO: Inform user if inputted string
        String ingredients = binding.etIngredientList.getText().toString();
        String instructions = binding.etInstructions.getText().toString();

        // TODO: Check photofile

        //TODO: Later update to TOAST messages regarding specific fields
        if (title.isEmpty() || cuisineType.isEmpty() || cooktime == 0 || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(),"Field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        publishRecipe(title, cuisineType,cooktime,ingredients,instructions);
    }

    private void publishRecipe(String title, String cuisineType, int cooktime, String ingredients, String instructions) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setCuisineType(cuisineType);
        recipe.setCooktime(cooktime);
        List<String> ingredientList = Arrays.asList(ingredients.split("\n"));
        Log.i(TAG, "Ingredient List Uploaded: " + ingredientList.toString());
        recipe.setIngredientList(ingredientList);
        List<String> instructionList = Arrays.asList(instructions.split("\n"));
        Log.i(TAG, "Instruction List Uploaded: " + instructionList.toString());
        recipe.setInstructions(instructionList);
        Log.i(TAG, "Finished inputing recipe details");
        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error with saving recipe",e);
                    Toast.makeText(getContext(), "Unable to save recipe!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"Successfully saved recipe: " + recipe.getTitle());
                binding.etRecipeName.setText("");
                binding.etCuisine.setText("");
                binding.etCooktime.setText("");
                binding.etIngredientList.setText("");
                binding.etInstructions.setText("");
            }
        });
    }

    private void goBackToUpload() {
        NavHostFragment.findNavController(this).navigate(R.id.uploadFragment);
    }
}