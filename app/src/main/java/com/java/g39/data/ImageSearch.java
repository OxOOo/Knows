package com.java.g39.data;

import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenyu on 2017/9/10.
 */

public class ImageSearch {
    private ImageSearch() {

    }

    private static Random r = new Random();

    /**
     * 搜索图片
     * @param keyword 关键词
     * @return 图片链接
     * @throws IOException
     */
    static String search(String keyword) throws IOException {
        return searchSogou(keyword);
    }

    static private String searchSogou(String keyword) throws IOException {
        long start = System.currentTimeMillis();
        String url = "http://pic.sogou.com/pics?query="+ URLEncoder.encode(keyword, "UTF-8");
        String res = API.GetBodyFromURL(url);
        System.out.println("Test " + (System.currentTimeMillis() - start));

        String image_url = null;
        int image_start = 0;

        final String[] patterns = {"\"thumbUrl\":\"(.*?)\""};
        List<String> images = new ArrayList<String>();
        for(String pattern: patterns) {
            Pattern reg = Pattern.compile(pattern);
            Matcher m = reg.matcher(res);
            for(int i = 0; m.find() && i < 10; i ++) {
                images.add(m.group(1));
            }
        }
        if (images.size() > 0) {
            image_url = images.get(r.nextInt(images.size()));
        }

        Log.d("Image Search", url);
        Log.d("IMAGE URL", keyword + " | " + image_url);
        return image_url;
    }

    /**
     * @deprecated 百度防止盗链
     * @param keyword 关键词
     * @return 第一张图片链接
     * @throws IOException
     */
    static private String searchBaidu(String keyword) throws IOException {
        long start = System.currentTimeMillis();
        String url = "http://image.baidu.com/search/index?tn=baiduimage&ct=201326592&word="+ URLEncoder.encode(keyword, "UTF-8");
        String res = API.GetBodyFromURL(url);
        System.out.println("Test " + (System.currentTimeMillis() - start));

        String image_url = null;
        int image_start = 0;

        final String[] patterns = {"\"pic\": \"(.*?)\"", "\"thumbURL\":\"(.*?)\""};
        for(String pattern: patterns) {
            Pattern reg = Pattern.compile(pattern);
            Matcher m = reg.matcher(res);
            if (m.find() && (image_url == null || image_start > m.start())) {
                image_url = m.group(1).replace("\\/", "/");
                image_start = m.start();
            }
        }

        Log.d("Image Search", url);
        Log.d("IMAGE URL", keyword + " | " + image_url);
        return image_url;
    }
}
