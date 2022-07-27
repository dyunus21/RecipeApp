package com.example.recipeapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ItemProfileRecipeBinding;
import com.example.recipeapp.models.Recipe;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private static final String TAG = "ProfileAdapter";
    private final Context context;
    private final List<Recipe> recipeList;
    private ItemProfileRecipeBinding item_binding;

    public ProfileAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipeList = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemProfileRecipeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Recipe recipe = recipeList.get(position);
        holder.bind(recipe);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_recipeDetailsFragment, bundle));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void clear() {
        recipeList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Recipe> list) {
        recipeList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProfileRecipeBinding binding;

        public ViewHolder(@NonNull ItemProfileRecipeBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Recipe recipe) {
            binding.tvRecipeName.setText(recipe.getTitle());

            String url = recipe.getImageUrl() == null ? recipe.getImage().getUrl() : recipe.getImageUrl();
            Glide.with(context).load(url).into(binding.ivRecipeImage);
        }
    }
}

