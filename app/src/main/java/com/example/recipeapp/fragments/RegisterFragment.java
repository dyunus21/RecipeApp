package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRegisterBinding;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;


public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        User user = new User();
        user.setEmail(binding.etEmail.getText().toString());
        user.setFirstName(binding.etFirstName.getText().toString());
        user.setLastName(binding.etLastName.getText().toString());
        user.setUsername(binding.etUsername.getText().toString());
        user.setPassword(binding.etPassword.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with registering user!");
                    Toast.makeText(getContext(), "Unable to register user!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Successfully registered user");
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "go to main");
        NavHostFragment.findNavController(this).navigate(R.id.recipeSearchFragment);
    }

    private void goLogin() {
        NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
    }
}