package com.example.recipeapp.models;

import android.graphics.Bitmap;

public class BitmapScaler {

    public static Bitmap scaleToFitWidth(final Bitmap b, final int width) {
        final float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

}

