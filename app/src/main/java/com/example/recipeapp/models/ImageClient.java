package com.example.recipeapp.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.recipeapp.activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ImageClient extends MainActivity {
    private static final String TAG = "ImageClient";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = Calendar.getInstance().getTime() + ".jpg";
    File photoFile;
    Context context;
    Fragment fragment;


    public ImageClient(Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }


    public void launchCamera() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        final Uri fileProvider = FileProvider.getUriForFile(context, "com.example.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
            fragment.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    public void onPickPhoto(View view) {
        Log.i(TAG, "onPickPhoto!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        fragment.startActivityForResult(intent, PICK_PHOTO_CODE);

    }

    public File resizeFile(Bitmap image) {
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

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to load image from URI", e);
        }
        return image;
    }

    public File getPhotoFile() {
        return photoFile;
    }
}
