package com.example.recipeapp.models;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.example.recipeapp.fragments.BarcodeScanFragment;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.internal.ImageConvertUtils;

import java.util.List;
import java.util.Objects;

// Resource: https://medium.com/codex/scan-barcodes-in-android-using-the-ml-kit-30b2a03ccd50
@SuppressLint("UnsafeOptInUsageError")
public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "BarcodeAnalyzer";
    @NonNull
    private final BarcodeScanFragment barcodeScanFragment;

    public BarcodeAnalyzer(final @NonNull BarcodeScanFragment fragment) {
        this.barcodeScanFragment = fragment;
    }

    @Override
    public void analyze(@NonNull final ImageProxy image) {
        final Image img = image.getImage();
        if (img != null) {
            final InputImage inputImage = InputImage.fromMediaImage(img, image.getImageInfo().getRotationDegrees());
            final BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_UPC_A).build();
            final BarcodeScanner scanner = BarcodeScanning.getClient(options);
            scanner.process(inputImage).addOnSuccessListener(barcodes -> {
                        Log.i(TAG, "Success barcode, ");
                        analyzeBarcodes(barcodes, inputImage);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to scan barcode", e))
                    .addOnCompleteListener(task -> {
                        Log.i(TAG, "Complete barcode");
                        image.close();
                    });
        }


    }

    private void analyzeBarcodes(@NonNull final List<Barcode> barcodes, @NonNull final InputImage inputImage) {
        for (final Barcode barcode : barcodes) {
            Log.i(TAG, barcode.toString());

            if (barcode.getValueType() == Barcode.TYPE_PRODUCT) {
                Log.i(TAG, "Barcode: " + barcode.getRawValue());
                Bitmap bitmap = null;
                try {
                    bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputImage);
                } catch (MlKitException e) {
                    Log.e(TAG, "Unable to fetch bitmap", e);
                }
                barcodeScanFragment.setBarcode(barcode, Objects.requireNonNull(bitmap));
            }
        }
    }


}
