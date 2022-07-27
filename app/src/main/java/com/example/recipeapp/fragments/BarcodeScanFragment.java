package com.example.recipeapp.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.R;
import com.example.recipeapp.clients.BarcodeAnalyzeClient;
import com.example.recipeapp.databinding.CameraDialogBinding;
import com.example.recipeapp.databinding.FragmentBarcodeScanBinding;
import com.example.recipeapp.models.BarcodeAnalyzer;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Headers;

public class BarcodeScanFragment extends Fragment {

    private static final String TAG = "BarcodeScanningFragment";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new ArrayList<>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)).toArray(new String[0]);
    @NonNull
    private final Ingredient ingredient = new Ingredient();
    @NonNull
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private FragmentBarcodeScanBinding binding;
    @Nullable
    private ProgressDialog progressDialog;
    private ExecutorService cameraExecutor;
    private ImageAnalysis imageAnalyzer;
    private Barcode barcode;
    @Nullable
    private Bitmap bitmap;
    @NonNull
    private String productName = "";

    public BarcodeScanFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        binding = FragmentBarcodeScanBinding.inflate(getLayoutInflater());
        binding.setFragmentBarcodeController(this);
        progressDialog = new ProgressDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (allPermissionsGranted()) {
            Log.i(TAG, "Start Camera!");
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private boolean allPermissionsGranted() {
        for (final String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user. ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(requireContext());
        processCameraProvider.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = processCameraProvider.get();
                final Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
                initImageAnalyzer();

                final CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalyzer);
            } catch (Exception e) {
                Log.e(TAG, "Use case binding failed");
                cameraExecutor.shutdown();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void initImageAnalyzer() {
        imageAnalyzer = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalyzer.setAnalyzer(cameraExecutor, new BarcodeAnalyzer(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private void getProductName(final String rawValue) {
        Log.i(TAG, "Getting product name for: " + rawValue);
        final BarcodeAnalyzeClient barcodeAnalyzeClient = new BarcodeAnalyzeClient(requireContext());
        barcodeAnalyzeClient.analyzeBarcode(rawValue, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, @NonNull JSON json) {
                Log.i(TAG, "Analyzed barcode!" + json);
                try {
                    productName = json.jsonObject.getString("item_name");
                    progressDialog.dismiss();
                    showAlert();
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to process ingredient name!", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Unable to analyze barcode" + throwable + " " + response);
            }
        });
    }

    public void showAlert() {
        final MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        final CameraDialogBinding cameraDialogBinding = CameraDialogBinding.inflate(getLayoutInflater());
        cameraDialogBinding.tvRawvalue.setText("Raw Value: " + barcode.getRawValue());
        cameraDialogBinding.tvProductName.setText("Product Name: " + productName);
        final ArrayAdapter<String> unitsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.units));
        cameraDialogBinding.actvUnit.setAdapter(unitsAdapter);
        Glide.with(requireContext()).load(bitmap).into(cameraDialogBinding.ivPreview);
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            startCamera();
        });
        alertDialogBuilder.setPositiveButton("Add Ingredient", (dialog, which) -> {
            Log.i(TAG, "Add Ingredient: " + cameraDialogBinding.tvProductName.getText().toString().substring(14));
            final String name = cameraDialogBinding.tvProductName.getText().toString().substring(14);
            final String count = Objects.requireNonNull(cameraDialogBinding.etCount.getText()).toString();
            final String unit = cameraDialogBinding.actvUnit.getText().toString();
            if (name.isEmpty() || count.isEmpty() || unit.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                ingredient.initialize(name, Integer.parseInt(count), unit);
                saveIngredient();
            }
        });
        alertDialogBuilder.setView(cameraDialogBinding.getRoot());
        alertDialogBuilder.show();
    }

    private void saveIngredient() {
        ingredient.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error in adding ingredient!", e);
                return;
            }
            final List<Ingredient> ingredientList = CURRENT_USER.getIngredientArray();
            ingredientList.add(ingredient);

            CURRENT_USER.setIngredientArray(ingredientList);
            CURRENT_USER.getParseUser().saveInBackground(e1 -> {
                if (e1 != null) {
                    Log.e(TAG, "Error in adding ingredient to user!", e1);
                    return;
                }
                Log.i(TAG, "Saved ingredient to user's ingredient list!");
                goToInventory();
            });
        });

    }

    public void goToInventory() {
        NavHostFragment.findNavController(this).navigate(R.id.inventoryFragment);
    }

    public void setBarcode(@NonNull final Barcode barcode, @NonNull final Bitmap bitmap) {
        imageAnalyzer.clearAnalyzer();
        this.barcode = barcode;
        this.bitmap = bitmap;
        progressDialog.setMessage("Searching for product...");
        progressDialog.show();
        getProductName(barcode.getRawValue());

    }
}