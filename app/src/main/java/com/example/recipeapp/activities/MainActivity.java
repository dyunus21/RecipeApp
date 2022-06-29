package com.example.recipeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public final static int PROFILE_PHOTO_CODE = 0;
    private static final String TAG = "MainActivity";
    public static NavController navController;
    public final User CURRENT_USER = new User(ParseUser.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            logoutUser();
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        File photoFile;
//        if ((data != null)) {
//            ImageClient imageClient = new ImageClient(this);
//            Uri photoUri = data.getData();
//            Bitmap selectedImage = imageClient.loadFromUri(photoUri);
//            photoFile = imageClient.getPhotoFileUri(imageClient.getFileName(photoUri));
//            photoFile = imageClient.resizeFile(selectedImage);
//            Log.i(TAG, "File: " + photoFile.toString());
//            if (requestCode == PROFILE_PHOTO_CODE) {
//                CURRENT_USER.setProfileImage(new ParseFile(photoFile));
//                CURRENT_USER.getParseUser().saveInBackground();
//                Log.i(TAG, "image: " + CURRENT_USER.getProfileImage().getUrl());
//            }
//        }
//    }
}