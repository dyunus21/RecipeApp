package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.databinding.ItemIngredientBinding;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.Post;

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
        item_binding = ItemIngredientBinding.inflate(LayoutInflater.from(context),parent,false);
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
            binding.tvCount.setText(ingredient.getCount());
        }
    }
}
