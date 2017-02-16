package com.relferreira.gitnotify.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * Created by relferreira on 2/11/17.
 */

public class RoundBitmapHelper {

    public static RoundedBitmapDrawable getRoundImage(BitmapDrawable img, Resources resources) {
        Bitmap imageBitmap = img.getBitmap();
        return getRoundImage(imageBitmap, resources);
    }

    public static RoundedBitmapDrawable getRoundImage(Bitmap imageBitmap, Resources resources) {
        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap);
        imageDrawable.setCircular(true);
        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
        return imageDrawable;
    }
}
