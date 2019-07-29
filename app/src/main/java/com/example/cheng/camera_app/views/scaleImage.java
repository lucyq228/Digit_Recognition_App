package com.example.cheng.camera_app.views;

import android.graphics.Bitmap;

/**
 * Created by cheng on 3/12/2018.
 */

//!!!Image not scaled well, due to the focus of the image.
    //!!!should consider other scale methods
    //https://www.codota.com/android/methods/android.graphics.Bitmap/createScaledBitmap

public class scaleImage {

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        int newWidth = 28;
        int newHeight = 28;
//        int origWidth = bitmap.getWidth();
//        int origHeight = bitmap.getHeight();

        // If no new width or height were specified return the original bitmap
//        if (newWidth <= 0 && newHeight <= 0) {
//            return bitmap;
//        }
        // Only the width was specified
//        else if (newWidth > 0 && newHeight <= 0) {
//            newHeight = (newWidth * origHeight) / origWidth;
//        }
        // only the height was specified
//        else if (newWidth <= 0 && newHeight > 0) {
//            newWidth = (newHeight * origWidth) / origHeight;
//        }
        // If the user specified both a positive width and height
        // (potentially different aspect ratio) then the width or height is
        // scaled so that the image fits while maintaining aspect ratio.
        // Alternatively, the specified width and height could have been
        // kept and Bitmap.SCALE_TO_FIT specified when scaling, but this
        // would result in whitespace in the new image.
//        else {
//            double newRatio = newWidth / (double) newHeight;
//            double origRatio = origWidth / (double) origHeight;
//
//            if (origRatio > newRatio) {
//                newHeight = (newWidth * origHeight) / origWidth;
//            } else if (origRatio < newRatio) {
//                newWidth = (newHeight * origWidth) / origHeight;
//            }
//        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
