package com.java.g39.news.newsdetail;

import android.app.Activity;

import com.java.g39.data.DetailNews;
import com.java.g39.data.Manager;

import io.reactivex.functions.Consumer;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsDetailPresenter implements NewsDetailContract.Presenter {

    private String mNews_ID;
    private NewsDetailContract.View mView;

    public NewsDetailPresenter(NewsDetailContract.View view, String news_ID) {
        this.mNews_ID = news_ID;
        this.mView = view;
        view.setPresenter(this);
        Manager.I.touchRead(mNews_ID);
    }

    @Override
    public void subscribe() {
        if (Manager.I.getConfig().isTextMode())
            mView.setImageVisible(false);
        Manager.I.fetchDetailNews(mNews_ID)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        if (detailNews == DetailNews.NULL) {
                            mView.setImageVisible(false);
                            mView.onError();
                        } else {
                            mView.setImageVisible(!Manager.I.getConfig().isTextMode());
                            mView.setNewsDetail(detailNews);
                        }
                    }
                });
        mView.onStartLoading();
    }

    @Override
    public void favorite(DetailNews news) {
        news.is_favorite = true;
        Manager.I.insertFavorite(news);
        mView.onShowToast("已添加收藏");
    }

    @Override
    public void unFavorite(DetailNews news) {
        news.is_favorite = false;
        Manager.I.removeFavorite(news.news_ID);
        mView.onShowToast("已取消收藏");
    }

    @Override
    public void shareNews(Activity activity, DetailNews news) {
        Manager.I.shareNews(activity, news.news_Title,
                news.news_Intro.isEmpty() ? news.news_Title : news.news_Intro, news.news_URL, news.picture_url);
    }

    @Override
    public void unsubscribe() {
        // nothing
    }
}
