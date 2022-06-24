package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.ItemRecipeCardBinding;
import com.example.recipeapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.ViewHolder> {
    private static final String TAG = "RecipeSearchAdapter";
    private Context context;
    public List<Recipe> recipesList = new ArrayList<>();
    private ItemRecipeCardBinding item_binding;

    public RecipeSearchAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipesList = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipesList.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    public void clear() {
        recipesList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Recipe> list) {
        recipesList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemRecipeCardBinding binding;
        public ViewHolder(@NonNull ItemRecipeCardBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Recipe recipe) {
            binding.tvTitle.setText(recipe.getTitle());
            // TODO: Add image to grid
             Glide.with(context).load(recipe.getImageUrl()).into(binding.ivImage);
            binding.tvCooktime.setText(recipe.getCooktime() + "m");
            binding.tvCuisine.setText(recipe.getCuisineType());
        }

        @Override
        public void onClick(View v) {

        }

    }
}
