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
import com.example.recipeapp.databinding.FragmentLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    public LoginFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Login!");
                loginUser();
            }
        });
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });

    }

    private void loginUser() {
        Log.i(TAG, "Attempting to login user");
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with login", e);
                    return;
                }

                goMainActivity();
                Toast.makeText(getContext(), "Welcome " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        NavHostFragment.findNavController(this).navigate(R.id.recipeSearchFragment);
    }

    private void goRegister() {
        NavHostFragment.findNavController(this).navigate(R.id.registerFragment);
    }


}