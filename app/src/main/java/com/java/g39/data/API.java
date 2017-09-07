package com.java.g39.data;

import android.util.*;

import com.java.g39.data.SimpleNews;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

import io.reactivex.Flowable;
import io.reactivex.annotations.*;
import io.reactivex.functions.*;
import io.reactivex.schedulers.*;

/**
 * Created by chenyu on 2017/9/7.
 * 新闻API相关操作
 */

public class API {
    private API() { }

    /**
     * @param json_news Json格式的SimpleNews
     * @return  SimpleNews
     * @throws JSONException
     */
    private  static SimpleNews GetNewsFromJson(JSONObject json_news) throws JSONException
    {
        SimpleNews news = new SimpleNews();
        news.lang_Type=json_news.getString("lang_Type");
        news.newsClassTag=json_news.getString("newsClassTag");
        news.news_Author=json_news.getString("news_Author");
        news.news_ID=json_news.getString("news_ID");
        news.news_Pictures=json_news.getString("news_Pictures");
        news.news_Source=json_news.getString("news_Source");
        news.news_Time=json_news.getString("news_Time");
        news.news_Title=json_news.getString("news_Title");
        news.news_URL=json_news.getString("news_URL");
        news.news_Video=json_news.getString("news_Video");
        news.news_Intro=json_news.getString("news_Intro");
        return news;
    }

    /**
     * @param url 网页地址
     * @return 网页内容
     */
    private static String GetBodyFromURL(String url)
    {
        try {
            URL cs = new URL(url);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(cs.openStream()));
            String inputLine, body = "";
            while ((inputLine = in.readLine()) != null)
                body = body + inputLine;
            in.close();
            return body;
        }
        catch (Exception e)
        {
            Log.d("NetAPI",e.toString());
            return "{}";
        }
    }


    /**
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @param category 分类，-1表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(final int pageNo,final int pageSize, final int category) {
        return Flowable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String url) throws Exception {
                            String URL_String = new String(String.format("http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d", pageNo, pageSize));
                            if (category != -1)
                                URL_String = URL_String + String.format("&category=%d", category);
                            return GetBodyFromURL(URL_String);
                    }
                }).flatMap(new Function<String, Flowable<SimpleNews>>() {
                    @Override
                    public Flowable<SimpleNews> apply(@NonNull String body) throws Exception {
                        List<SimpleNews> result = new ArrayList<SimpleNews>();
                        JSONObject allData = new JSONObject(body);
                        System.out.println("body:"+body);
                        JSONArray list = allData.getJSONArray("list");
                        System.out.println("body:"+body);
                        for(int t=0;t<list.length();t++)
                        {
                            JSONObject json_news = list.getJSONObject(t);
                            result.add(GetNewsFromJson(json_news));
                        }
                        return Flowable.fromIterable(result);
                    }
                });
    }

    /**
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize)
    {
        return GetSimpleNews(pageNo, pageSize, -1);
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @param category 分类，-1表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(final String keyword, final int pageNo, final int pageSize, final int category)
    {
        return Flowable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String url) throws Exception {
                        String URL_String = new String(String.format("http://166.111.68.66:2042/news/action/query/search?keyword=%s&pageNo=1&pageSize=%d&category=%d", keyword, pageNo, pageSize));
                        if (category != -1)
                            URL_String = URL_String + String.format("&category=%d", category);
                        return GetBodyFromURL(URL_String);
                    }
                }).flatMap(new Function<String, Flowable<SimpleNews>>() {
                    @Override
                    public Flowable<SimpleNews> apply(@NonNull String body) throws Exception {
                        List<SimpleNews> result = new ArrayList<SimpleNews>();
                        JSONObject allData = new JSONObject(body);
                        JSONArray list = allData.getJSONArray("list");
                        for(int t=0;t<list.length();t++)
                        {
                            JSONObject json_news = list.getJSONObject(t);
                            result.add(GetNewsFromJson(json_news));
                        }
                        return Flowable.fromIterable(result);
                    }
                });
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(String keyword, int pageNo, int pageSize)
    {
        return SearchNews(keyword, pageNo, pageSize, -1);
    }

    /**
     * 获取新闻详情，不设置subscribeOn，只设置网络获取的字段
     * @param newsId ID
     * @return 新闻详情
     */
    public static Flowable<DetailNews> GetDetailNews(String newsId)
    {
        // FIXME
        return null;
    }
}
