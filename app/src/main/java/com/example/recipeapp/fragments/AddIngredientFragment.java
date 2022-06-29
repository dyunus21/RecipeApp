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
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.databinding.FragmentAddIngredientBinding;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class AddIngredientFragment extends Fragment {
    private static final String TAG = "AddIngredientFragment";
    private FragmentAddIngredientBinding binding;
    public User CURRENT_USER = new User(ParseUser.getCurrentUser());

    public AddIngredientFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddIngredientBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        String name = binding.etName.getText().toString();
        String count = binding.etCount.getText().toString();
        String unit = binding.etUnit.getText().toString();
        if(name.isEmpty() || count.isEmpty() || unit.isEmpty()) {
            Toast.makeText(getContext(), "Field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        addIngredient(name,count, unit);
    }

    private void addIngredient(String name, String count, String unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.initialize(name,Integer.parseInt(count), unit);
        ingredient.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Log.e(TAG,"Error in adding ingredient!",e);
                    return;
                }
                List<Ingredient> ingredientList = CURRENT_USER.getIngredientList();
                ingredientList.add(ingredient);
                CURRENT_USER.setIngredientList(ingredientList);
                CURRENT_USER.getParseUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null) {
                            Log.e(TAG, "Error in adding ingredient to user!",e);
                            return;
                        }
                        Log.i(TAG, "Saved ingredient to user's ingredient list!");
                        binding.etName.setText("");
                        binding.etCount.setText("");
                        binding.etUnit.setText("");
                goToInventory();
                    }
                });
            }
        });

    }

    private void goToInventory() {
        NavHostFragment.findNavController(this).navigate(R.id.inventoryFragment);
    }
}