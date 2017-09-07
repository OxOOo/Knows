package com.java.g39.newsdetail;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsDetailPresenter implements NewsDetailContract.Presenter {

    private String news_ID;
    private NewsDetailContract.View view;

    public NewsDetailPresenter(String news_ID, NewsDetailContract.View view) {
        this.news_ID = news_ID;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // FIXME
    }

    @Override
    public void unsubscribe() {
        // nothing
    }
}
