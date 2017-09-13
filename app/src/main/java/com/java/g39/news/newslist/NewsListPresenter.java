package com.java.g39.news.newslist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.java.g39.data.DetailNews;
import com.java.g39.data.Manager;
import com.java.g39.data.SimpleNews;
import com.java.g39.news.newsdetail.NewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private final int PAGE_SIZE = 20;

    private NewsListContract.View mView;
    private int mCategory;
    private String mKeyword;
    private int mPageNo = 1;
    private boolean mLoading = false;

    public NewsListPresenter(NewsListContract.View view, int category, String keyword) {
        this.mView = view;
        this.mCategory = category;
        this.mKeyword = keyword;

        view.setPresenter(this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public void setKeyword(String keyword) {
        mKeyword = keyword;
        refreshNews();
    }

    @Override
    public void subscribe() {
        refreshNews();
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void requireMoreNews() {
        mPageNo++;
        fetchNews();
    }

    @Override
    public void refreshNews() {
        mPageNo = 1;
        fetchNews();
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
    public void fetchNewsRead(final int pos, SimpleNews news) {
        Manager.I.fetchDetailNews(news.news_ID)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        mView.resetItemRead(pos, detailNews.has_read);
                    }
                });
    }

    private void fetchNews() {
        mLoading = true;
        final long start = System.currentTimeMillis();

        Single<List<SimpleNews>> single = null;
        if (mKeyword.trim().length() > 0) {
            single = Manager.I.searchNews(mKeyword, mPageNo, PAGE_SIZE, mCategory);
        } else if (mCategory > 0) {
            single = Manager.I.fetchSimpleNews(mPageNo, PAGE_SIZE, mCategory);
        } else {
            single = Manager.I.recommend();
        }

        single.subscribe(new Consumer<List<SimpleNews>>() {
            @Override
            public void accept(List<SimpleNews> simpleNewses) throws Exception {
                System.out.println(System.currentTimeMillis() - start + " | " + mCategory + " | " + simpleNewses.size());
                mLoading = false;
                if (mKeyword.trim().length() != 0 || mCategory > 0) {
                    mView.onSuccess(simpleNewses.size() == 0); // TODO check if load completed
                    // TODO onError
                    if (mPageNo == 1) mView.setNewsList(simpleNewses);
                    else mView.appendNewsList(simpleNewses);
                } else {
                    if (mPageNo > 1 || simpleNewses.size() == 0) {
                        mView.onSuccess(true);
                        mView.appendNewsList(new ArrayList<>());
                    } else {
                        mView.onSuccess(false);
                        mView.setNewsList(simpleNewses);
                    }
                }
            }
        });
    }
}
