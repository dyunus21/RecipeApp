package com.example.recipeapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.clients.ImageClient;
import com.example.recipeapp.databinding.FragmentUploadPostBinding;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.Objects;

public class UploadPostFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "FragmentUploadPost";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    public ImageClient imageClient;
    @Nullable
    private File photoFile;
    private FragmentUploadPostBinding binding;
    @Nullable
    private ProgressDialog progressDialog;
    private Recipe recipe;

    public UploadPostFragment() {
        // NO-OP
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageClient = new ImageClient(this);
        progressDialog = new ProgressDialog(requireContext());
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentUploadPostBinding.inflate(getLayoutInflater());
        binding.setFragmentUploadPostController(this);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG, "Received bundle: " + recipe.getRecipeId());
        }

        return binding.getRoot();
    }

    public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int pos, final long id) {
        parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(@NonNull final AdapterView<?> parent) {

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.postOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerType.setAdapter(adapter);
        binding.spinnerType.setOnItemSelectedListener(this);
        if (recipe != null) {
            Log.i(TAG, "Received recipe in Upload post!");
            binding.etRecipeLink.setText(recipe.getObjectId() + " " + recipe.getTitle());
        }

    }

    public void validateRecipe() {
        Objects.requireNonNull(progressDialog).setMessage("Uploading post...");
        progressDialog.show();
        String title = binding.etTitle.getText().toString();
        String description = binding.etDescription.getText().toString();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Field cannot be empty!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if (photoFile == null || binding.ivImage.getDrawable() == null) {
            Toast.makeText(requireContext(), "Post does not contain any image!", Toast.LENGTH_SHORT).show();
            return;
        }
        postRecipe(title, description);
    }

    private void postRecipe(@NonNull final String title, @NonNull final String description) {
        Post post = new Post();
        post.setAuthor(new User(ParseUser.getCurrentUser()));
        post.setImage(new ParseFile(photoFile));
        post.setTitle(title);
        post.setDescription(description);
        post.setType(binding.spinnerType.getSelectedItem().toString());
        if (recipe != null) {
            post.setRecipeLinked(recipe);
        }
        savePost(post);
    }

    private void savePost(@NonNull final Post post) {
        post.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error in saving post", e);
                Toast.makeText(requireContext(), "Unable to save post!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i(TAG, "Successfully saved post: " + post.getTitle());
            binding.etTitle.setText("");
            binding.etDescription.setText("");
            binding.ivImage.setImageResource(0);
            Objects.requireNonNull(progressDialog).dismiss();
            final Bundle bundle = new Bundle();
            bundle.putParcelable(Recipe.class.getSimpleName(), recipe);
            NavHostFragment.findNavController(this).navigate(R.id.socialFeedFragment, bundle);
        });
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        photoFile = imageClient.getPhotoFile();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "onActivity result camera");
            if (resultCode == RESULT_OK) {
                final Bitmap takenImage = BitmapFactory.decodeFile(Objects.requireNonNull(photoFile).getAbsolutePath());
                photoFile = imageClient.resizeFile(takenImage);
                Glide.with(requireContext()).load(photoFile).into(binding.ivImage);
                Log.i(TAG, "File: " + photoFile.toString());
            } else {
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            final Uri photoUri = data.getData();
            final Bitmap selectedImage = imageClient.loadFromUri(photoUri);
            photoFile = imageClient.resizeFile(selectedImage);
            Glide.with(requireContext()).load(photoFile).into(binding.ivImage);
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }

}
