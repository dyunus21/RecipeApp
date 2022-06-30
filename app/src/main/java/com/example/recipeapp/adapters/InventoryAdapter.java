package com.example.recipeapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.databinding.ItemIngredientBinding;
import com.example.recipeapp.models.Ingredient;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private static final String TAG = "Inventory Adapter";
    private final Context context;
    private final List<Ingredient> ingredientList;
    private ItemIngredientBinding item_binding;

    public InventoryAdapter(Context context, List<Ingredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemIngredientBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    public void addAll(List<Ingredient> list) {
        ingredientList.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemIngredientBinding binding;
        private Ingredient currentIngredient;

        public ViewHolder(@NonNull ItemIngredientBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Ingredient ingredient) {
            currentIngredient = ingredient;
            binding.tvName.setText(ingredient.getName());
            binding.tvCount.setText(String.valueOf(ingredient.getCount()));
            binding.tvUnit.setText(ingredient.getUnit());
            binding.ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add(ingredient);
                }
            });
            binding.ibSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(ingredient);
                }
            });
        }

        private void add(Ingredient ingredient) {
            int count = ingredient.getCount() + 1;
            ingredient.setCount(count);
            binding.tvCount.setText(String.valueOf(count));
            ingredient.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error in updating ingredient");
                        return;
                    }
                    Log.i(TAG, "Ingredient count: " + ingredient.getCount());
                }
            });
        }

        private void remove(Ingredient ingredient) {
            int count = ingredient.getCount() > 0 ? ingredient.getCount() - 1 : 0;
            ingredient.setCount(count);
            binding.tvCount.setText(String.valueOf(count));
            ingredient.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error in updating ingredient");
                        return;
                    }
                    Log.i(TAG, "Ingredient count: " + ingredient.getCount());
                }
            });
        }
    }


}
