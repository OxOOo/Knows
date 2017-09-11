package com.java.g39.favorites;

import android.content.Intent;
import android.os.Bundle;

import com.java.g39.data.Manager;
import com.java.g39.data.SimpleNews;
import com.java.g39.news.newsdetail.NewsDetailActivity;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by equation on 9/12/17.
 */

public class FavoritesPresenter implements FavoritesContract.Presenter {

    private FavoritesContract.View mView;
    private int mPageNo = 1;
    private boolean mLoading = false;

    public FavoritesPresenter(FavoritesContract.View view) {
        this.mView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getFavorites();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void openNewsDetailUI(SimpleNews news, Bundle options) {
        Intent intent = new Intent(mView.context(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.NEWS_ID, news.news_ID);
        intent.putExtra(NewsDetailActivity.NEWS_TITLE, news.news_Title);
        intent.putExtra(NewsDetailActivity.NEWS_PICTURE_URL, news.picture_url);
        intent.putExtra(NewsDetailActivity.NEWS_IS_FAVORITED, news.is_favorite);
        mView.start(intent, options);
    }

    @Override
    public void getFavorites() {
        Manager.I.favorites()
                .subscribe(new Consumer<List<SimpleNews>>() {
                    @Override
                    public void accept(List<SimpleNews> list) throws Exception {
                        mView.setFavorites(list);
                    }
                });
    }
}
