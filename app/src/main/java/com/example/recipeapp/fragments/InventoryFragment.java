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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.InventoryAdapter;
import com.example.recipeapp.databinding.FragmentInventoryBinding;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    private static final String TAG = "InventoryFragment";
    private FragmentInventoryBinding binding;
    private List<Ingredient> ingredientList;
    private InventoryAdapter adapter;


    public InventoryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientList = new ArrayList<>();
        adapter = new InventoryAdapter(getContext(), ingredientList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvIngredients.setAdapter(adapter);
        binding.rvIngredients.setLayoutManager(linearLayoutManager);
        User user = ((MainActivity) getActivity()).CURRENT_USER;

        adapter.clear();
        ingredientList = user.getIngredientList();
        adapter.notifyDataSetChanged();
        Log.i(TAG, ingredientList.toString());

        binding.ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddIngredient();
            }
        });

    }

    private void goToAddIngredient() {
        NavHostFragment.findNavController(this).navigate(R.id.addIngredientFragment);
    }


}