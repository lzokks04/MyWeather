package com.lzokks04.myweather.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 獲取json的類
 * Created by Liu on 2016/8/12.
 */
public class NetUtil {

    /**
     * 读取流
     *
     * @param
     * @return String
     */
    private static String readStream(InputStream is) {
        InputStreamReader isr = null;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 获取json源数据
     *
     * @param url
     * @return
     */
    public static String getJsonData(String url) {
        String result = "";
        try {
            result = readStream(new URL(url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据url获取bitmap
     *
     * @param imageUri
     * @return
     */
    public static void getBitmap(final String imageUri, final NetUtilBitmapCallBack listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    URL myFileUrl = new URL(imageUri);
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null) {
                        listener.onSuccess(bitmap);
                    }
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure(e);
                }
            }
        }).start();
    }


}
