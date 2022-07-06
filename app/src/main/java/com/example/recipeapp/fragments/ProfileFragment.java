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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.recipeapp.activities.RegisterActivity;
import com.example.recipeapp.adapters.ProfileAdapter;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.example.recipeapp.models.BitmapScaler;
import com.example.recipeapp.models.ImageClient;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int PROFILE_PHOTO_CODE = 0;
    private final static int PICK_PHOTO_CODE = 1046;
    private final String photoFileName = "photo.jpg";
    private File photoFile;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser()); // TODO: FIX THIS
    private FragmentProfileBinding binding;
    private ProfileAdapter adapter;
    private List<Recipe> recipes;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipes = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), recipes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvUsername.setText("@" + CURRENT_USER.getParseUser().getUsername());
        binding.tvFullname.setText(CURRENT_USER.getFirstName() + " " + CURRENT_USER.getLastName());
        Glide.with(getContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        binding.tvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        binding.rvUploadedRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUploadedRecipes.setAdapter(adapter);
        queryRecipes("uploaded");
    }

    private void queryRecipes(String type) {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        if (type.equals("uploaded")) {
            query.whereEqualTo(Recipe.KEY_AUTHOR, CURRENT_USER.getParseUser());
        }
        query.include(Recipe.KEY_IMAGE_URL);
        query.include(Recipe.KEY_IMAGE);
        query.include(Recipe.KEY_INGREDIENT_LIST);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting uploaded recipes", e);
                    return;
                }
                adapter.clear();
                recipes.addAll(objects);
                adapter.notifyDataSetChanged();
                binding.tvRecipeCount.setText(String.valueOf(recipes.size()));

            }
        });
    }


    private void setProfileImage() {
        CURRENT_USER.setProfileImage(new ParseFile(photoFile));
        CURRENT_USER.getParseUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with saving profile image!", e);
                    Toast.makeText(getContext(), "Unable to save profile image. Please try again!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Glide.with(getContext()).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
                    Toast.makeText(getContext(), "Successfully saved profile image!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            photoFile = getPhotoFileUri(getFileName(photoUri));
            photoFile = resizeFile(selectedImage);
            setProfileImage();
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }

    public void onPickPhoto(View view) {
        Log.i(TAG, "onPickPhoto!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public File resizeFile(final Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
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

    @SuppressLint("Range")
    public String getFileName(final Uri uri) {
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

    public File getPhotoFileUri(final String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Bitmap loadFromUri(final Uri photoUri) {
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
            Log.e(TAG, "Unable to load image from URI", e);
        }
        return image;
    }
}