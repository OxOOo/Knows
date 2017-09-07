package com.java.g39.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chenyu on 2017/9/7.
 */

public class FS {
    private FS() {}

    /**
     * 下载图片
     * @param url 图片链接
     * @return 图片
     * @throws IOException
     */
    public static Bitmap DownloadImage(String url) throws IOException {
        URL imgUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();

        return bitmap;
    }
}
