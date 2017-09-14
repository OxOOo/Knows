package com.java.g39.data;

import android.graphics.Bitmap;

import io.reactivex.Single;

/**
 * Created by chenyu on 2017/9/7.
 */

public class SimpleNews {
    public String plain_json; // 原始json字符串

    public String lang_Type;
    public String newsClassTag;
    public String news_Author;
    public String news_ID;
    public String news_Intro = "";
    public String news_Pictures;
    public String news_Source;
    public String news_Time;
    public String news_Title;
    public String news_URL;
    public String news_Video;

    public Single<String> single_picture_url; // 解析出的图片链接，已设置subscribeOn(Schedulers.io())，未设置observeOn
    public String picture_url; // 最终显示的图片
    public boolean has_read = false; // 是否已读
    public boolean is_favorite = false; // 是否已收藏

    public boolean from_disk; // 是否是从磁盘上读取的
    public boolean from_search = false; // 是否是搜索

    public String formatTime() {
        try {
            if (news_Time.length() == 14) {
                return news_Time.substring(0, 4) + "-" + news_Time.substring(4, 6) + "-" + news_Time.substring(6, 8);
            } else {
                return news_ID.substring(0, 4) + "-" + news_ID.substring(4, 6) + "-" + news_ID.substring(6, 8);
            }
        } catch(Exception e) {
            return "0000-00-00";
        }
    }
}
