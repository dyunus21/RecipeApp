package com.example.recipeapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.databinding.ActivityLoginBinding;
import com.example.recipeapp.main.MainActivity;
import com.parse.ParseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setActivityLoginController(this);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }
    }

    public void loginUser() {
        Log.i(TAG, "Attempting to login user");
        final String username = binding.etUsername.getText().toString();
        final String password = binding.etPassword.getText().toString();
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "Error with login" + e.getMessage());
                if (Objects.equals(e.getMessage(), "Invalid username/password.")) {
                    Toast.makeText(LoginActivity.this, "Invalid username/password ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            goMainActivity();
            Toast.makeText(LoginActivity.this, "Welcome " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
        });
    }

    private void goMainActivity() {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goRegister() {
        final Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }
}
