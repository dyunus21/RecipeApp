package com.example.recipeapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ActivityRegisterBinding;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Register!");
                registerUser();
            }
        });
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
            }
        });
    }

    private void registerUser() {
        Log.i(TAG, "Attempting to register user");
        String email = binding.etEmail.getText().toString();
        String firstName = binding.etFirstName.getText().toString();
        String lastName = binding.etLastName.getText().toString();
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with registering user!");
                    Toast.makeText(RegisterActivity.this, "Unable to register user!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Successfully registered user");
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}