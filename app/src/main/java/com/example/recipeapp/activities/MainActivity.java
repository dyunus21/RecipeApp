package com.example.recipeapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static NavController navController;
    public User CURRENT_USER = new User(ParseUser.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        User.getUser(CURRENT_USER);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setTitle("Logout from app?");
            alertDialogBuilder.setMessage("You will need to log back in to access the app!");
            alertDialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logoutUser();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        Log.i(TAG, "Attempting to logout user!");
        ParseUser.logOut();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

}
