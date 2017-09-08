package com.java.g39.data;

import android.util.*;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.annotations.*;
import io.reactivex.functions.*;

/**
 * Created by chenyu on 2017/9/7.
 * 新闻API相关操作
 */

public class API {
    private API() {
    }

    /**
     * @param json_news Json格式的SimpleNews
     * @return DetailNews
     * @throws JSONException
     */
    private static DetailNews GetDetailNewsFromJson(JSONObject json_news) throws JSONException {
        JSONArray list;
        DetailNews news = new DetailNews();
        news.Keywords = new ArrayList<DetailNews.WordWithScore>();
        list = json_news.getJSONArray("Keywords");
        for (int t = 0; t < list.length(); t++) {
            DetailNews.WordWithScore item = news.new WordWithScore();
            JSONObject jobj = list.getJSONObject(t);
            item.word = jobj.getString("word");
            item.score = jobj.getDouble("score");
            news.Keywords.add(item);
        }
        news.bagOfWords = new ArrayList<DetailNews.WordWithScore>();
        list = json_news.getJSONArray("bagOfWords");
        for (int t = 0; t < list.length(); t++) {
            DetailNews.WordWithScore item = news.new WordWithScore();
            JSONObject jobj = list.getJSONObject(t);
            item.word = jobj.getString("word");
            item.score = jobj.getDouble("score");
            news.bagOfWords.add(item);
        }
        news.crawl_Source = json_news.getString("crawl_Source");
        news.crawl_Time = json_news.getString("crawl_Time");
        news.inborn_KeyWords = json_news.getString("inborn_KeyWords");
        news.lang_Type = json_news.getString("lang_Type");
        news.locations = new ArrayList<DetailNews.WordWithCount>();
        list = json_news.getJSONArray("locations");
        for (int t = 0; t < list.length(); t++) {
            DetailNews.WordWithCount item = news.new WordWithCount();
            JSONObject jobj = list.getJSONObject(t);
            item.word = jobj.getString("word");
            item.count = jobj.getInt("count");
            news.locations.add(item);
        }
        news.newsClassTag = json_news.getString("newsClassTag");
        news.news_Author = json_news.getString("news_Author");
        news.news_Category = json_news.getString("news_Category");
        news.news_Content = json_news.getString("news_Content");
        news.news_ID = json_news.getString("news_ID");
        news.news_Journal = json_news.getString("news_Journal");
        news.news_Pictures = json_news.getString("news_Pictures");
        news.news_Source = json_news.getString("news_Source");
        news.news_Time = json_news.getString("news_Time");
        news.news_Title = json_news.getString("news_Title");
        news.news_URL = json_news.getString("news_URL");
        news.news_Video = json_news.getString("news_Video");
        news.organizations = new ArrayList<String>();
        list = json_news.getJSONArray("organizations");
        for (int t = 0; t < list.length(); t++)
            news.organizations.add(list.getString(t));
        news.persons = new ArrayList<DetailNews.WordWithCount>();
        list = json_news.getJSONArray("persons");
        for (int t = 0; t < list.length(); t++) {
            DetailNews.WordWithCount item = news.new WordWithCount();
            JSONObject jobj = list.getJSONObject(t);
            item.word = jobj.getString("word");
            item.count = jobj.getInt("count");
            news.persons.add(item);
        }
        news.repeat_ID = json_news.getString("repeat_ID");
        news.seggedPListOfContent = new ArrayList<String>();
        list = json_news.getJSONArray("seggedPListOfContent");
        for (int t = 0; t < list.length(); t++)
            news.seggedPListOfContent.add(list.getString(t));
        news.seggedTitle = json_news.getString("seggedTitle");
        news.wordCountOfContent = json_news.getInt("wordCountOfContent");
        news.wordCountOfTitle = json_news.getInt("wordCountOfTitle");
        return news;
    }

    /**
     * @param json_news Json格式的DetailNews
     * @return DetailNews
     * @throws JSONException
     */
    private static SimpleNews GetNewsFromJson(JSONObject json_news) throws JSONException {
        SimpleNews news = new SimpleNews();
        news.lang_Type = json_news.getString("lang_Type");
        news.newsClassTag = json_news.getString("newsClassTag");
        news.news_Author = json_news.getString("news_Author");
        news.news_ID = json_news.getString("news_ID");
        news.news_Pictures = json_news.getString("news_Pictures");
        news.news_Source = json_news.getString("news_Source");
        news.news_Time = json_news.getString("news_Time");
        news.news_Title = json_news.getString("news_Title");
        news.news_URL = json_news.getString("news_URL");
        news.news_Video = json_news.getString("news_Video");
        news.news_Intro = json_news.getString("news_Intro");
        return news;
    }

    /**
     * @param url 网页地址
     * @return 网页内容
     */
    private static String GetBodyFromURL(String url) throws IOException {
        URL cs = new URL(url);
        BufferedReader in = new BufferedReader(new
                InputStreamReader(cs.openStream()));
        String inputLine, body = "";
        while ((inputLine = in.readLine()) != null)
            body = body + inputLine;
        in.close();
        return body;
    }


    /**
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     *
     * @param pageNo   页码
     * @param pageSize 每页新闻数量
     * @param category 分类，0表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String URL_String = new String(String.format("http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d", pageNo, pageSize));
                    if (category > 0)
                        URL_String = URL_String + String.format("&category=%d", category);
                    return GetBodyFromURL(URL_String);
                }
            }).flatMap(new Function<String, Flowable<SimpleNews>>() {
                    @Override
                    public Flowable<SimpleNews> apply(@NonNull String body) throws Exception {
                    List<SimpleNews> result = new ArrayList<SimpleNews>();
                    try {
                        JSONObject allData = new JSONObject(body);
                        JSONArray list = allData.getJSONArray("list");
                        for (int t = 0; t < list.length(); t++) {
                            JSONObject json_news = list.getJSONObject(t);
                            result.add(GetNewsFromJson(json_news));
                        }
                    } catch (Exception e) {
                        Log.e("error", "error in API.GetSimpleNews Json_body:" + body);
                    }
                    return Flowable.fromIterable(result);
                }
            });
    }

    /**
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     *
     * @param pageNo   页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize) {
        return GetSimpleNews(pageNo, pageSize, 0);
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     *
     * @param keyword  关键字
     * @param pageNo   页码
     * @param pageSize 每页新闻数量
     * @param category 分类，0表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(final String keyword, final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String URL_String = new String(String.format("http://166.111.68.66:2042/news/action/query/search?keyword=%s&pageNo=%d&pageSize=%d", keyword, pageNo, pageSize));
                    if (category > 0)
                        URL_String = URL_String + String.format("&category=%d", category);
                    return GetBodyFromURL(URL_String);
                }
            }).flatMap(new Function<String, Flowable<SimpleNews>>() {
                    @Override
                    public Flowable<SimpleNews> apply(@NonNull String body) throws Exception {
                    List<SimpleNews> result = new ArrayList<SimpleNews>();
                    JSONObject allData;
                    try {
                        allData = new JSONObject(body);
                        JSONArray list = allData.getJSONArray("list");
                        for (int t = 0; t < list.length(); t++) {
                            JSONObject json_news = list.getJSONObject(t);
                            result.add(GetNewsFromJson(json_news));
                        }
                    } catch (Exception e) {
                        Log.e("error", "error in API.SearchNews Json_body:" + body);
                    }
                    return Flowable.fromIterable(result);
                }
            });
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     *
     * @param keyword  关键字
     * @param pageNo   页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(String keyword, int pageNo, int pageSize) {
        return SearchNews(keyword, pageNo, pageSize, 0);
    }

    /**
     * 获取新闻详情，不设置subscribeOn，只设置网络获取的字段
     *
     * @param newsId ID
     * @return 新闻详情
     */
    public static Flowable<DetailNews> GetDetailNews(final String newsId) {
        return Flowable.fromCallable(new Callable<DetailNews>() {
                @Override
                public DetailNews call() throws Exception {
                    String URL_String = new String(String.format("http://166.111.68.66:2042/news/action/query/detail?newsId=%s", newsId));
                    String body = GetBodyFromURL(URL_String);
                    List<SimpleNews> result = new ArrayList<SimpleNews>();
                    try {
                        JSONObject allData;
                        Log.d("tag", "body:" + body);
                        allData = new JSONObject(body);
                        Log.d("tag", "body:" + body);
                        return GetDetailNewsFromJson(allData);

                    } catch (Exception e) {
                        Log.e("error", "error in API.GetDetailNews Json_body:" + body);
                        Log.e("error", e.toString());
                    }
                    return null;
                }
            });
    }
}
