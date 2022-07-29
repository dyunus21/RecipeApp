package com.example.recipeapp.clients;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.recipeapp.main.MainActivity;
import com.example.recipeapp.models.BitmapScaler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageClient extends MainActivity {
    private static final String TAG = "ImageClient";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo.jpg";
    @NonNull
    final Context context;
    @NonNull
    final Fragment fragment;
    File photoFile;


    public ImageClient(@NonNull final Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.requireContext();
    }


    public void launchCamera() {
        photoFileName = Math.random() + photoFileName;
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        final Uri fileProvider = FileProvider.getUriForFile(context, "com.example.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            fragment.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    public void onPickPhoto() {
        photoFileName = Math.random() + photoFileName;
        Log.i(TAG, "onPickPhoto!");
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        fragment.startActivityForResult(intent, PICK_PHOTO_CODE);

    }

    @NonNull
    public File resizeFile(@NonNull final Bitmap image) {
        final Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to create new file ", e);
        }
        Log.i(TAG, "File: " + resizedFile);
        return resizedFile;
    }

    @NonNull
    public File getPhotoFileUri(final String fileName) {
        final File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Bitmap loadFromUri(@NonNull final Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            final ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
            image = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            Log.e(TAG, "Unable to load image from URI", e);
        }
        return image;
    }

    @Nullable
    public File getPhotoFile() {
        return photoFile;
    }
}
