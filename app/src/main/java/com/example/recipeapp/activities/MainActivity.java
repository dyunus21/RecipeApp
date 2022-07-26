package com.example.recipeapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ActivityMainBinding;
import com.example.recipeapp.models.FabAnimation;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    public static NavController navController;
    public User CURRENT_USER = new User(ParseUser.getCurrentUser());
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);
        binding.setController(this);

        setSupportActionBar(binding.toolbar);
        User.getUser(CURRENT_USER);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        binding.bottomNavigationView.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.teal_700)));
        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        FabAnimation.init(binding.fabRecipe);
        FabAnimation.init(binding.fabPost);
        final boolean[] isRotate = {false};
        binding.fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab(v, isRotate);
            }
        });
        binding.fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.uploadPostFragment);
                binding.fabUpload.callOnClick();
            }
        });
    }

    public void animateFabRecipeOnClick() {
        navController.navigate(R.id.uploadRecipeFragment);
        binding.fabUpload.callOnClick();
    }

    private void animateFab(View v, boolean[] isRotate) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.logout) {
//            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
//            alertDialogBuilder.setTitle("Logout from app?");
//            alertDialogBuilder.setMessage("You will need to log back in to access the app!");
//            alertDialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    logoutUser();
//                }
//            });
//            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            alertDialogBuilder.show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
