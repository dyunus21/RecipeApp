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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.databinding.FragmentUploadRecipeBinding;
import com.example.recipeapp.models.ImageClient;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class UploadRecipeFragment extends Fragment {
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public final static int PICK_PHOTO_CODE = 1046;
    private static final String TAG = "FragmentUploadRecipe";
    private static final User currentUser = new User(ParseUser.getCurrentUser());
    private File photoFile;
    private Recipe recipe;
    private boolean edited = false;
    private FragmentUploadRecipeBinding binding;
    private ProgressDialog progressDialog;
    private ImageClient imageClient;

    public UploadRecipeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        User.getUser(currentUser);
        imageClient = new ImageClient(this);
        final Bundle bundle = this.getArguments();
        progressDialog = new ProgressDialog(getContext());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Upload New Recipe");
        if (bundle != null) {
            edited = true;
            recipe = bundle.getParcelable("Recipe");
            Log.i(TAG, "Received bundle: " + recipe.getRecipeId());
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Edit: " + recipe.getTitle());
            getRecipe();
            User.getUser(recipe.getAuthor());
        } else {
            recipe = new Recipe();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadRecipeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (edited) {
            binding.btnDelete.setVisibility(View.VISIBLE);
        }
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe();
            }
        });

        if (recipe.getTitle() != null) {
            initializePage();
        }
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ibBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
            }
        });

        binding.btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClient.launchCamera();
            }
        });
        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageClient.onPickPhoto(v);
            }
        });
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading recipe...");
                progressDialog.show();
                validateRecipe();
            }
        });


    }

    private void deleteRecipe() {
        recipe.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to delete recipe: " + recipe.getTitle(), e);
                    return;
                }
                Log.i(TAG, "Successfully deleted recipe" + recipe.getTitle());
                List<Recipe> uploaded = currentUser.getRecipesUploaded();
                uploaded.remove(recipe);
                currentUser.setRecipesUploaded(uploaded);
                currentUser.saveInBackground();
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.recipeSearchFragment);
            }
        });
    }

    private void initializePage() {
        binding.etRecipeName.setText(recipe.getTitle());
        Glide.with(getContext()).load(recipe.getImage().getUrl()).into(binding.ivImage);
        binding.etCuisine.setText(recipe.getCuisineType());
        binding.etCooktime.setText(String.valueOf(recipe.getCooktime()));
        String instructions = "";
        for (int i = 0; i < recipe.getInstructions().size(); i++) {
            instructions += recipe.getInstructions().get(i) + "\n";
        }
        binding.etInstructions.setText(instructions);
        String ingredients = "";
        for (int i = 0; i < recipe.getIngredientList().size(); i++) {
            ingredients += recipe.getIngredientList().get(i) + "\n";
        }
        binding.etIngredientList.setText(ingredients);
        return;
    }

    private void validateRecipe() {
        final String title = binding.etRecipeName.getText().toString();
        final String cuisineType = binding.etCuisine.getText().toString();
        final int cooktime = Integer.parseInt(binding.etCooktime.getText().toString());
        final String ingredients = binding.etIngredientList.getText().toString();
        final String instructions = binding.etInstructions.getText().toString();

        //TODO: Later update to TOAST messages regarding specific fields
        if (title.isEmpty() || cuisineType.isEmpty() || cooktime == 0 || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(), "Field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile == null && binding.ivImage.getDrawable() == null) {
            Toast.makeText(getContext(), "Post does not contain any image!", Toast.LENGTH_SHORT).show();
            return;
        }
        publishRecipe(title, cuisineType, cooktime, ingredients, instructions);
    }

    private void publishRecipe(final String title, final String cuisineType, final int cooktime, final String ingredients, final String instructions) {
        recipe.setTitle(title);
        recipe.setCuisineType(cuisineType);
        recipe.setCooktime(cooktime);
        recipe.setAuthor(new User(ParseUser.getCurrentUser()));
        List<String> ingredientList = Arrays.asList(ingredients.split("\n"));
        Log.i(TAG, "Ingredient List Uploaded: " + ingredientList);
        recipe.setIngredientList(ingredientList);
        List<String> instructionList = Arrays.asList(instructions.split("\n"));
        Log.i(TAG, "Instruction List Uploaded: " + instructionList);
        recipe.setInstructions(instructionList);
        if (photoFile != null)
            recipe.setImage(new ParseFile(photoFile));
        Log.i(TAG, "Finished inputting recipe details");
        recipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with saving recipe", e);
                    Toast.makeText(getContext(), "Unable to save recipe!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Successfully saved recipe: " + recipe.getTitle());
                clearPage();
                if (!edited)
                    addRecipeToUser(recipe);
                progressDialog.dismiss();
            }
        });

    }

    private void clearPage() {
        binding.etRecipeName.setText("");
        binding.etCuisine.setText("");
        binding.etCooktime.setText("");
        binding.etIngredientList.setText("");
        binding.etInstructions.setText("");
        binding.ivImage.setImageResource(0);
    }

    public void getRecipe() {
        ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
        query.include(Recipe.KEY_TITLE);
        query.include(Recipe.KEY_COOKTIME);
        query.include(Recipe.KEY_CUISINE_TYPE);
        query.include(Recipe.KEY_AUTHOR);
        query.include(Recipe.KEY_RECIPE_ID);
        query.include(Recipe.KEY_INGREDIENT_LIST);
        query.include(Recipe.KEY_INSTRUCTIONS);
        query.include(Recipe.KEY_IMAGE);
        query.include(Recipe.KEY_IMAGE_URL);
        query.include(Recipe.KEY_REVIEWS);
        query.getInBackground(recipe.getObjectId(), new GetCallback<Recipe>() {
            @Override
            public void done(Recipe object, ParseException e) {
                setRecipe(object);
            }
        });
    }

    private void setRecipe(Recipe object) {
        this.recipe = object;
        return;
    }

    private void addRecipeToUser(Recipe recipe) {
        List<Recipe> uploaded = currentUser.getRecipesUploaded();
        if (!uploaded.contains(recipe))
            uploaded.add(recipe);
        currentUser.setRecipesUploaded(uploaded);
        currentUser.getParseUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in saving recipe to user uploaded recipe array", e);
                }
                Log.i(TAG, "Successfully saved recipe to user recipe array");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        photoFile = imageClient.getPhotoFile();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "onActivity result camera");
            if (resultCode == RESULT_OK) {
                final Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                photoFile = imageClient.resizeFile(takenImage);
                Log.i(TAG, "File: " + photoFile.toString());
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            final Uri photoUri = data.getData();
            Bitmap selectedImage = imageClient.loadFromUri(photoUri);
            photoFile = imageClient.resizeFile(selectedImage);
            Log.i(TAG, "File: " + photoFile.toString());
        }
    }
}
