package com.java.g39.newslist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import com.java.g39.R;
import com.java.g39.data.API;
import com.java.g39.data.SimpleNews;
import com.java.g39.newsdetail.NewsDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;
    private int mCategory;
    private int mPageNo = 1;

    public NewsListPresenter(NewsListContract.View view, int category) {
        this.mView = view;
        this.mCategory = category;
        this.mPageNo = 1;

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
        mPageNo = 1;
        fetchNews();
    }

    @Override
    public void refreshNews() {
        mPageNo ++;
        fetchNews();
    }

    @Override
    public void openNewsDetailUI(SimpleNews news) {
        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.NEWS_ID, news.news_ID);
        mView.start(intent);
    }

    private void fetchNews() {
        API.GetSimpleNews(mPageNo, 20, mCategory)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SimpleNews>>() {
                    @Override
                    public void accept(List<SimpleNews> simpleNewses) throws Exception {
                        mView.appendNewsList(simpleNewses);
                    }
                });
    }
}
