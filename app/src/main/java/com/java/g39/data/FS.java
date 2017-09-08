package com.java.g39.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chenyu on 2017/9/7.
 * 磁盘
 */

class FS {
    private FS() {}

    /**
     * 下载图片，尝试3次，如果3次均不能正常下载，则返回null
     * @param url 图片链接
     * @return 图片
     */
    public static Bitmap DownloadImage(String url) {
        Log.d("URL", url);
        for(int times = 3; times > 0; times --) {
            try {
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                return bitmap;
            } catch(IOException e) {
                Log.d("URL_ERROR", url);
                e.printStackTrace();
            }
        }

        return null;
    }
}
