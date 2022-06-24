package com.example.recipeapp.models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.recipeapp.activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageClient extends MainActivity {
    public int photo_code;
    private static final String TAG = "ImageClient";
    public String photoFileName = "photo.jpg";
    File photoFile;
    Context context;

    public ImageClient(Context context) {
        this.context = context;
    }


    public void onPickPhoto(View view, int photo_code) {
        Log.i(TAG, "onPickPhoto!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i(TAG, "start intent for gallery!");
        this.photo_code = photo_code;
        ((Activity)context).startActivityForResult(intent, photo_code);

    }

    public File resizeFile(Bitmap image) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(image, 800);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = getPhotoFileUri(photoFileName);
        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Unable to create new file ", e);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found ", e);
        }
        try {
            fos.write(bytes.toByteArray());
        } catch (IOException e) {
            Log.e(TAG, "Unable to write to file ", e);
        }
        try {
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to close file ", e);
        }
        Log.i(TAG, "File: " + resizedFile);
        return resizedFile;
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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
}
