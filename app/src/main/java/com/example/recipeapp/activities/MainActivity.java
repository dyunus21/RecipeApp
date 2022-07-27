package com.example.recipeapp.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ActivityMainBinding;
import com.example.recipeapp.models.FabAnimation;
import com.example.recipeapp.models.User;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static NavController navController;
    public final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);
        binding.setController(this);

        User.getUser(CURRENT_USER);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        binding.bottomNavigationView.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.teal_700)));
        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        FabAnimation.init(binding.fabRecipe);
        FabAnimation.init(binding.fabPost);
        final boolean[] isRotate = {false};
        binding.fabUpload.setOnClickListener(v -> animateFab(v, isRotate));
        binding.fabPost.setOnClickListener(v -> {
            navController.navigate(R.id.uploadPostFragment);
            binding.fabUpload.callOnClick();
        });
    }

    public void animateFabRecipeOnClick() {
        navController.navigate(R.id.uploadRecipeFragment);
        binding.fabUpload.callOnClick();
    }

    private void animateFab(final @NonNull View v, @NonNull boolean[] isRotate) {
        isRotate[0] = FabAnimation.rotateFab(v, !isRotate[0]);
        if (isRotate[0]) {
            FabAnimation.showIn(binding.fabRecipe);
            FabAnimation.showIn(binding.fabPost);
        } else {
            FabAnimation.showOut(binding.fabRecipe);
            FabAnimation.showOut(binding.fabPost);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
