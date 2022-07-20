package com.example.recipeapp.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.recipeapp.BarcodeAnalyzeClient;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.CameraDialogBinding;
import com.example.recipeapp.databinding.FragmentBarcodeScanBinding;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Headers;

public class BarcodeScanFragment extends Fragment {

    private static final String TAG = "BarcodeScanningFragment";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    private final String photoFileName = "photo.jpg";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)).toArray(new String[0]);
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final Ingredient ingredient = new Ingredient();
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private Bitmap imageBitmap;
    private FragmentBarcodeScanBinding binding;
    private File photoFile;
    private ProgressDialog progressDialog;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private InputImage inputImage;
    private String rawValue;

    public BarcodeScanFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBarcodeScanBinding.inflate(getLayoutInflater());
        progressDialog = new ProgressDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (allPermissionsGranted()) {
            Log.i(TAG, "Start Camera!");
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        binding.btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private boolean allPermissionsGranted() {
        for (final String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user. ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(getContext());
        processCameraProvider.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = processCameraProvider.get();
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
                    imageCapture = new ImageCapture.Builder().build();
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);
                } catch (Exception e) {
                    Log.e(TAG, "Use case binding failed");
                    cameraExecutor.shutdown();
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private void takePhoto() {
        if (imageCapture == null) {
            Log.i(TAG, "imageCapture is null");
            return;
        }
        Log.i(TAG, "inTakePhoto");
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(getContext().getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(getContext(), "Photo capture succeeded: " + outputFileResults.getSavedUri(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Photo capture succeeded: " + outputFileResults.getSavedUri());
                try {
                    inputImage = InputImage.fromFilePath(getContext(), outputFileResults.getSavedUri());
                    showAlert(outputFileResults.getSavedUri());
                } catch (IOException e) {
                    Log.e(TAG, "Unable to generate inputimage from uri", e);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage());
            }
        });
    }

    private void scanBarcodes(InputImage image, View view) {
        Log.i(TAG, "Scanning Barcode...");
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC,
                                Barcode.FORMAT_ALL_FORMATS,
                                Barcode.FORMAT_UPC_A)
                        .build();
        BarcodeScanner scanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        Log.i(TAG, "Success barcode, ");
                        for (Barcode barcode : barcodes) {
                            Log.i(TAG, barcode.toString());
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            if (valueType == Barcode.TYPE_PRODUCT) {
                                rawValue = barcode.getRawValue();
                                Log.i(TAG, "Barcode: " + rawValue);
                                ((TextView) view.findViewById(R.id.tvRawvalue)).setText("Raw Value: " + rawValue);
                                getProductName(rawValue, view);
                            } else {
                                Toast.makeText(getContext(), "Barcode not detected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to scan barcode", e);
                    }
                }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                        Log.i(TAG, "Complete barcode");
                        progressDialog.dismiss();
                    }
                });
    }

    private void getProductName(String rawValue, View view) {
        BarcodeAnalyzeClient barcodeAnalyzeClient = new BarcodeAnalyzeClient(getContext());
        barcodeAnalyzeClient.analyzeBarcode(rawValue, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Analyzed barcode!" + json.toString());
                try {
                    ((TextView) view.findViewById(R.id.tvProductName)).setText("Product Name: " + json.jsonObject.getString("item_name"));
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

    private void showAlert(Uri uri) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        CameraDialogBinding cameraDialogBinding = CameraDialogBinding.inflate(getLayoutInflater());
        Glide.with(getContext()).load(uri).into(cameraDialogBinding.ivPreview);
        cameraDialogBinding.btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Scanning barcode...");
                progressDialog.show();
                scanBarcodes(inputImage, cameraDialogBinding.getRoot());
                cameraDialogBinding.tvResultsText.setVisibility(View.VISIBLE);
                cameraDialogBinding.tvRawvalue.setVisibility(View.VISIBLE);
                cameraDialogBinding.tvProductName.setVisibility(View.VISIBLE);
                cameraDialogBinding.tilCount.setVisibility(View.VISIBLE);
                cameraDialogBinding.tilUnit.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setPositiveButton("Add Ingredient", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Add Ingredient: " + cameraDialogBinding.tvProductName.getText().toString().substring(14));
                String name = cameraDialogBinding.tvProductName.getText().toString().substring(14);
                String count = cameraDialogBinding.etCount.getText().toString();
                String unit = cameraDialogBinding.etUnit.getText().toString();
                if (name.isEmpty() || count.isEmpty() || unit.isEmpty()) {
                    Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                addIngredient(cameraDialogBinding, name, count, unit);
            }
        });
        alertDialogBuilder.setView(cameraDialogBinding.getRoot());
        alertDialogBuilder.show();
    }

    private void addIngredient(CameraDialogBinding cameraDialogBinding, final String name, final String count, final String unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.initialize(name, Integer.parseInt(count), unit);
        ingredient.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in adding ingredient!", e);
                    return;
                }
                List<Ingredient> ingredientList = CURRENT_USER.getIngredientArray();
                ingredientList.add(ingredient);

                CURRENT_USER.setIngredientArray(ingredientList);
                CURRENT_USER.getParseUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error in adding ingredient to user!", e);
                            return;
                        }
                        Log.i(TAG, "Saved ingredient to user's ingredient list!");
                        goToInventory();
                    }
                });
            }
        });

    }

    private void goToInventory() {
        NavHostFragment.findNavController(this).navigate(R.id.inventoryFragment);
    }
}