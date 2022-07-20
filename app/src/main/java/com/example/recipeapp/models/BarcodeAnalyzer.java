package com.example.recipeapp.models;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.example.recipeapp.fragments.BarcodeScanFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.internal.ImageConvertUtils;

import java.util.List;

// Resource: https://medium.com/codex/scan-barcodes-in-android-using-the-ml-kit-30b2a03ccd50
@SuppressLint("UnsafeOptInUsageError")
public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "BarcodeAnalyzer";
    private final BarcodeScanFragment barcodeScanFragment;

    public BarcodeAnalyzer(BarcodeScanFragment fragment) {
        this.barcodeScanFragment = fragment;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        Image img = image.getImage();
        if (img != null) {
            InputImage inputImage = InputImage.fromMediaImage(img, image.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_UPC_A).build();
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
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
                                    Log.i(TAG, "Barcode: " + barcode.getRawValue());
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputImage);
                                    } catch (MlKitException e) {
                                        e.printStackTrace();
                                    }
                                    barcodeScanFragment.setBarcode(barcode, bitmap);
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
                            image.close();
                        }
                    });
        }


    }


}
