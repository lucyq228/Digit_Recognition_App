package com.example.cheng.camera_app.views;

import android.graphics.Bitmap;

/**
 * Created by cheng on 3/11/2018.
 */

public class DrawView2 {

    public static float[] getPixelData(Bitmap mBitmap){

        Bitmap mOffscreenBitmap2 = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        //resize picture to 28 * 28
        Bitmap mOffscreenBitmap = scaleImage.scaleBitmap(mOffscreenBitmap2);

        //recognition not work for below code
        //Bitmap mOffscreenBitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);

        if (mOffscreenBitmap == null) {
            return null;
        }

        int width = mOffscreenBitmap.getWidth();
        int height = mOffscreenBitmap.getHeight();

        // Get 28x28 pixel data from bitmap
        int[] pixels = new int[width * height];
        mOffscreenBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        float[] retPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            int b = pix & 0xff;
            retPixels[i] = (float)((0xff - b)/255.0);
        }
        return retPixels;
    }

}
