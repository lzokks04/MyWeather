package com.lzokks04.myweather.util;

import android.graphics.Bitmap;

/**
 * Created by Liu on 2016/8/15.
 */
public interface NetUtilBitmapCallBack {
    void onSuccess(Bitmap bitmap);

    void onFailure(Exception e);
}
