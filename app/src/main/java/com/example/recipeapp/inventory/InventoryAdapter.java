package com.example.recipeapp.inventory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.databinding.ItemIngredientBinding;
import com.example.recipeapp.models.parse.Ingredient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private static final String TAG = "Inventory Adapter";
    private final Context context;
    private final List<Ingredient> ingredientList;
    private ItemIngredientBinding item_binding;

    public InventoryAdapter(@NonNull final Context context, @NonNull final List<Ingredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        item_binding = ItemIngredientBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Ingredient ingredient = ingredientList.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public void clear() {
        ingredientList.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull final List<Ingredient> list) {
        ingredientList.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientBinding binding;
        private Ingredient currentIngredient;

        public ViewHolder(@NonNull final ItemIngredientBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(@NonNull final Ingredient ingredient) {
            currentIngredient = ingredient;
            binding.tvName.setText(ingredient.getName());
            binding.tvCount.setText(String.valueOf(ingredient.getCount()));
            binding.tvUnit.setText(ingredient.getUnit());
            binding.ibAdd.setOnClickListener(v -> add(ingredient));
            binding.ibSubtract.setOnClickListener(v -> remove(ingredient));
            binding.ibDelete.setOnClickListener(v -> {
                final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
                alertDialogBuilder.setMessage("Do you want to delete this ingredient: " + currentIngredient.getName() + "?");
                alertDialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
                    Log.i(TAG, "Deleted ingredient: " + currentIngredient.getName());
                    deleteIngredient();
                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                    Log.i(TAG, "Dismissed delete ingredient");
                    dialog.dismiss();
                });
                alertDialogBuilder.show();
            });
        }

        private void deleteIngredient() {
            currentIngredient.deleteInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Unable to delete ingredient");
                    return;
                }
                Log.i(TAG, "Successfully deleted ingredient");
                ingredientList.remove(currentIngredient);
                notifyDataSetChanged();
            });
        }

        private void add(@NonNull final Ingredient ingredient) {
            final int count = ingredient.getCount() + 1;
            ingredient.setCount(count);
            binding.tvCount.setText(String.valueOf(count));
            ingredient.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error in updating ingredient");
                    return;
                }
                Log.i(TAG, "Ingredient count: " + ingredient.getCount());
            });
        }

        private void remove(@NonNull final Ingredient ingredient) {
            final int count = ingredient.getCount() > 0 ? ingredient.getCount() - 1 : 0;
            ingredient.setCount(count);
            binding.tvCount.setText(String.valueOf(count));
            ingredient.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error in updating ingredient");
                    return;
                }
                Log.i(TAG, "Ingredient count: " + ingredient.getCount());
            });
        }
    }


}

