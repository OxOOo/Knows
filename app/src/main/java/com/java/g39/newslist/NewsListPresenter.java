package com.java.g39.newslist;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View view;

    public NewsListPresenter(NewsListContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        this.refreshNews();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void requireMoreNews() {
        List<SimpleNews> list = new ArrayList<SimpleNews>();
        for(int i = 0; i < 10; i ++) {
            try {
                list.add(createNews());
            } catch(JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }
        view.appendNewsList(list);
    }

    @Override
    public void refreshNews() {
        List<SimpleNews> list = new ArrayList<SimpleNews>();
        for(int i = 0; i < 10; i ++) {
            try {
                list.add(createNews());
            } catch(JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }
        view.setNewsList(list);
    }

    @Override
    public void openNewsDetailUI(SimpleNews news) {
        // FIXME
    }

    private SimpleNews createNews() throws JSONException {
        SimpleNews news = new SimpleNews();
        JSONObject obj = new JSONObject("{\"lang_Type\":\"zh-CN\",\"newsClassTag\":\"科技\",\"news_Author\":\"创事记 微博 作者： 广州阿超\",\"news_ID\":\"20160913041301d5fc6a41214a149cd8a0581d3a014f\",\"news_Pictures\":\"\",\"news_Source\":\"新浪新闻\",\"news_Time\":\"20160912000000\",\"news_Title\":\"iPhone 7归来，友商们吊打苹果的姿势正确吗？\",\"news_URL\":\"http://tech.sina.com.cn/zl/post/detail/mobile/2016-09-12/pid_8508491.htm\",\"news_Video\":\"\",\"news_Intro\":\"　　欢迎关注“创事记”的微信订阅号：sinachuangshiji 文/罗超...\"}");
        news.lang_Type = obj.getString("lang_Type");
        news.newsClassTag = obj.getString("newsClassTag");
        news.news_Author = obj.getString("news_Author");
        news.news_ID = obj.getString("news_ID");
        news.news_Intro = obj.getString("news_Intro");
        news.news_Pictures = obj.getString("news_Pictures");
        news.news_Source = obj.getString("news_Source");
        news.news_Time = obj.getString("news_Time");
        news.news_Title = obj.getString("news_Title");
        news.news_URL = obj.getString("news_URL");
        news.news_Video = obj.getString("news_Video");

        news.has_read = false;
        news.picture = BitmapFactory.decodeResource(view.getContext().getResources(), R.mipmap.ic_launcher);

        return news;
    }
}
