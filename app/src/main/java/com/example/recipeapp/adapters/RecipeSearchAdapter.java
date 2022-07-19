package com.example.recipeapp.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.Fade;
import androidx.transition.TransitionSet;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.databinding.ItemRecipeCardBinding;
import com.example.recipeapp.fragments.RecipeDetailsFragment;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.ParseUser;

import java.util.List;

class DetailsTransition extends TransitionSet {
    public DetailsTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}

public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.ViewHolder> {
    private static final String TAG = "RecipeSearchAdapter";
    private final Context context;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    public List<Recipe> recipesList;
    private ItemRecipeCardBinding item_binding;

    public RecipeSearchAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipesList = recipes;
    }

    //TODO: Check animations
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Recipe recipe = recipesList.get(position);
        holder.bind(recipe);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
        RecipeDetailsFragment details = new RecipeDetailsFragment();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details.setSharedElementEnterTransition(new DetailsTransition());
                details.setEnterTransition(new Fade());
                details.setExitTransition(new Fade());
                details.setSharedElementReturnTransition(new DetailsTransition());
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.binding.ivImage, "shared_image")
                        .replace(R.id.nav_host_fragment, details)
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeSearchFragment_to_recipeDetailsFragment, bundle));
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRecipeCardBinding binding;
        private Recipe currentRecipe;

        public ViewHolder(@NonNull ItemRecipeCardBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Recipe recipe) {
            currentRecipe = recipe;
            binding.tvTitle.setText(recipe.getTitle());
            if (recipe.getImageUrl() == null)
                Glide.with(context).load(recipe.getImage().getUrl()).into(binding.ivImage);
            else
                Glide.with(context).load(recipe.getImageUrl()).into(binding.ivImage);
            binding.tvCookTime.setText(recipe.getCooktime() + "m");
            binding.tvCuisine.setText(recipe.getCuisineType());
        }

    }
}
