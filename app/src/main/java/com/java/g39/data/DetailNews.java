package com.java.g39.data;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;

/**
 * Created by chenyu on 2017/9/7.
 */

public class DetailNews extends SimpleNews {
    public class WordWithScore {
        public String word;
        public double score;
    }
    public class WordWithCount {
        public String word;
        public int count;
    }

    public List<WordWithScore> Keywords;
    public List<WordWithScore> bagOfWords;
    public String crawl_Source;
    public String crawl_Time;
    public String inborn_KeyWords;
    public List<WordWithCount> locations;
    public String news_Category;
    public String news_Content;
    public String news_Journal;
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

    /**
     * @return 返回所有有必要添加超链接的词，以及其对应的超链接
     */
    public Map<String,String> getKeywordHyperlink() throws UnsupportedEncodingException {
        HashMap<String,String> result = new HashMap<String,String>();
        Pattern p = Pattern.compile(" *(.*?)(/PER|/LOC)");
        for(String s : seggedPListOfContent)
        {
            Matcher m = p.matcher(s);
            while (m.find()) {
                String key=m.group(1);
                result.put(key,"http://baike.baidu.com/item/"+ URLEncoder.encode(key, "UTF-8"));
            }
        }
        return result;
    }
}
