package com.example.recipeapp.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ActivityRegisterBinding;
import com.example.recipeapp.main.MainActivity;
import com.example.recipeapp.models.BitmapScaler;
import com.example.recipeapp.models.parse.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private final static int PICK_PHOTO_CODE = 1046;
    private static final String TAG = "RegisterActivity";
    private File photoFile;
    private ActivityRegisterBinding binding;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setActivityRegisterController(this);
    }

    public void registerUser() {
        Log.i(TAG, "Attempting to register user");
        final User user = new User(new ParseUser());
        user.getParseUser().setEmail(binding.etEmail.getText().toString());
        user.setFirstName(binding.etFirstName.getText().toString());
        user.setLastName(binding.etLastName.getText().toString());
        user.getParseUser().setUsername(binding.etUsername.getText().toString());
        user.getParseUser().setPassword(binding.etPassword.getText().toString());
        registerUserInParse(user);

    }

    private void registerUserInParse(@NonNull final User user) {
        user.getParseUser().signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with registering user!", e);
                Toast.makeText(RegisterActivity.this, "Unable to register user!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "Successfully registered user");
            if (photoFile != null) {
                setProfileImage(user);
            }
            goMainActivity();
            Toast.makeText(RegisterActivity.this, "Welcome " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setProfileImage(@NonNull final User user) {
        user.setProfileImage(new ParseFile(photoFile));
        user.getParseUser().saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with saving profile image!", e);
                Toast.makeText(RegisterActivity.this, "Unable to save profile image. Please try again!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Successfully saved profile image!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            photoFile = getPhotoFileUri(getFileName(photoUri));
            photoFile = resizeFile(selectedImage);
            Glide.with(this).load(photoUri).circleCrop().placeholder(R.drawable.ic_baseline_account_circle_24).into(binding.ivProfileImage);
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }

    public void onPickPhoto() {
        Log.i(TAG, "onPickPhoto!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    @NonNull
    public File resizeFile(@NonNull final Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String photoFileName = "photo.jpg";
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
            FileOutputStream fos = null;
            fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to create new file ", e);
        }
        Log.i(TAG, "File: " + resizedFile);
        return resizedFile;
    }

    @Nullable
    @SuppressLint("Range")
    public String getFileName(@NonNull final Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
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

    @NonNull
    public File getPhotoFileUri(@NonNull final String fileName) {
        final File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Bitmap loadFromUri(@NonNull final Uri photoUri) {
        Bitmap image = null;
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
            image = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            Log.e(TAG, "Unable to load image from URI", e);
        }
        return image;
    }


    private void goMainActivity() {
        final Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goLogin() {
        final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
