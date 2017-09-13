package com.java.g39.data;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;

import io.reactivex.Single;

/**
 * Created by chenyu on 2017/9/7.
 */

public class DetailNews extends SimpleNews {
    public static DetailNews NULL = new DetailNews();

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

    public Single<Map<String, String>> links; // 链接，字->链接 ，已设置subscribeOn(Schedulers.io())，未设置observeOn

    /**
     * @return 返回所有有必要添加超链接的词，以及其对应的超链接
     */
    Map<String,String> getKeywordHyperlink(AC_AutoMaton ac) throws UnsupportedEncodingException {
        Map<String,String> result = new HashMap<String,String>();
        Pattern p = Pattern.compile("\\s(\\S+?)(/PER|/LOC)");
        for(String s : seggedPListOfContent)
        {
            Matcher m = p.matcher(s);
            while (m.find()) {
                String key=m.group(1);
                result.put(key, "https://baike.baidu.com/item/"+ URLEncoder.encode(key, "UTF-8"));
            }
        }
        return result;
    }

    void loadKeywords(String keywords) throws JSONException {
        JSONArray array = new JSONArray(keywords);
        Keywords = new ArrayList<>();
        for(int i = 0; i < array.length(); i ++) {
            WordWithScore item = new WordWithScore();
            JSONObject jobj = array.getJSONObject(i);
            item.word = jobj.getString("word");
            item.score = jobj.getDouble("score");
            Keywords.add(item);
        }
    }
}
