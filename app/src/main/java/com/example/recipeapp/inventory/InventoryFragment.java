package com.example.recipeapp.inventory;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.AddIngredientDialogBinding;
import com.example.recipeapp.databinding.FragmentInventoryBinding;
import com.example.recipeapp.models.parse.Ingredient;
import com.example.recipeapp.models.parse.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryFragment extends Fragment {
    private static final String TAG = "InventoryFragment";
    private FragmentInventoryBinding binding;
    private List<Ingredient> ingredientList;
    private InventoryAdapter adapter;
    private User currentUser;
    @Nullable
    private ProgressDialog progressDialog;

    public InventoryFragment() {
        // NO-OP
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredientList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        adapter = new InventoryAdapter(requireContext(), ingredientList);

    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(getLayoutInflater());
        binding.setFragmentInventoryController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvIngredients.setAdapter(adapter);
        binding.rvIngredients.setLayoutManager(linearLayoutManager);

        final ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo(User.KEY_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.include(User.KEY_INGREDIENT_ARRAY);
        query.include(User.KEY_PROFILE_IMAGE);

        Log.i(TAG, "User id: " + ParseUser.getCurrentUser().getObjectId());
        try {
            final ParseUser parseUser = query.get(ParseUser.getCurrentUser().getObjectId());
            currentUser = new User(parseUser);
            Log.i(TAG, "done " + parseUser.getUsername());
            adapter.clear();
            ingredientList = currentUser.getIngredientArray();
            adapter.addAll(ingredientList);
        } catch (ParseException e) {
            Log.e(TAG, "No user found");
        }
        binding.tvNumIngredients.setText(ingredientList.size() + " items");
    }

    public void goToBarcodeScan() {
        NavHostFragment.findNavController(this).navigate(R.id.barcodeScanFragment);
    }

    public void showAddIngredientDialog() {
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        final AddIngredientDialogBinding ingredientDialogBinding = AddIngredientDialogBinding.inflate(getLayoutInflater());
        alertDialogBuilder.setView(ingredientDialogBinding.getRoot());
        final ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.units));
        ingredientDialogBinding.actvUnit.setAdapter(unitsAdapter);
        alertDialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            Objects.requireNonNull(progressDialog).show();
            final String name = Objects.requireNonNull(ingredientDialogBinding.etName.getText()).toString();
            final String count = Objects.requireNonNull(ingredientDialogBinding.etCount.getText()).toString();
            final String unit = ingredientDialogBinding.actvUnit.getText().toString();
            if (name.isEmpty() || count.isEmpty() || unit.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            addIngredient(name, count, unit);
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.show();
    }

    private void addIngredient(final String name, @NonNull final String count, final String unit) {
        final Ingredient ingredient = new Ingredient();
        ingredient.initialize(name, Integer.parseInt(count), unit);
        ingredient.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error in adding ingredient!", e);
                return;
            }
            final List<Ingredient> ingredientList = currentUser.getIngredientArray();
            adapter.clear();
            ingredientList.add(0,ingredient);
            adapter.addAll(ingredientList);
            binding.tvNumIngredients.setText(ingredientList.size() + " items");
            Objects.requireNonNull(progressDialog).dismiss();
            currentUser.setIngredientArray(ingredientList);
            currentUser.getParseUser().saveInBackground(e1 -> {
                if (e1 != null) {
                    Log.e(TAG, "Error in adding ingredient to user!", e1);
                    return;
                }
                Log.i(TAG, "Saved ingredient to user's ingredient list!");
            });
        });

    }

}
