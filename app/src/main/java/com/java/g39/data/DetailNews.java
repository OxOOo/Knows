package com.java.g39.data;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 */

public class DetailNews {
    public class WordWithScore {
        public String word;
        public double score;
    }
    public class WordWithCount {
        public String word;
        public int count;
    }

    public String plain_json; // 原始json字符串

    public List<WordWithScore> Keywords;
    public List<WordWithScore> bagOfWords;
    public String crawl_Source;
    public String crawl_Time;
    public String inborn_KeyWords;
    public String lang_Type;
    public List<WordWithCount> locations;
    public String newsClassTag;
    public String news_Author;
    public String news_Category;
    public String news_Content;
    public String news_ID;
    public String news_Journal;
    public String news_Pictures;
    public String news_Source;
    public String news_Time;
    public String news_Title;
    public String news_URL;
    public String news_Video;
    public List<String> organizations;
    public List<WordWithCount> persons;
    public String repeat_ID;
    public List<String> seggedPListOfContent;
    public String seggedTitle;
    public int wordCountOfContent;
    public int wordCountOfTitle;

    public String picture_url; // 解析出的图片链接，可能为null
    public boolean has_read; // 是否已读
    public boolean is_favorite; // 是否已收藏

    public boolean from_disk; // 是否是从磁盘上读取的
}
