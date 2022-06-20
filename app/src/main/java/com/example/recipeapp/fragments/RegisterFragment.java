package com.example.recipeapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.FragmentRegisterBinding;
import com.example.recipeapp.models.BitmapScaler;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RegisterFragment extends Fragment {

    public final static int PICK_PHOTO_CODE = 1046;
    private static final String TAG = "RegisterFragment";
    public String photoFileName = "photo.jpg";
    File photoFile;
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
        binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(view);
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
                    Log.e(TAG, "Issue with registering user!", e);
                    Toast.makeText(getContext(), "Unable to register user!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Successfully registered user");
                    if (photoFile != null) {
                        setProfileImage(user);
                    }
                    goMainActivity();
                }
            }
        });


    }

    private void setProfileImage(User user) {
        user.setProfileImage(new ParseFile(photoFile));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with saving profile image!", e);
                    Toast.makeText(getContext(), "Unable to save profile image. Please try again!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getContext(), "Successfully saved profile image!", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            photoFile = getPhotoFileUri(getFileName(photoUri));
            photoFile = resizeFile(selectedImage);
            Glide.with(getContext()).load(photoUri).circleCrop().into(binding.ivProfileImage);
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }

    public File resizeFile(Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "File: " + resizedFile);
//        binding.ivProfileImage.setImageBitmap(resizedBitmap);
        return resizedFile;
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void onPickPhoto(View view) {
        Log.i(TAG, "onPickPhoto!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }
}