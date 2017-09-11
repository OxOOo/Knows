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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by chenyu on 2017/9/7.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;
    private int mCategory;
    private int mPageNo = 1;
    private boolean mLoading = false;

    public NewsListPresenter(NewsListContract.View view, int category) {
        this.mView = view;
        this.mCategory = category;

        view.setPresenter(this);
    }

    @Override
    public boolean isLoading() {
        return mLoading;
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
        mPageNo ++;
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
        if (mCategory > 0) {
            Manager.I.fetchSimpleNews(mPageNo, 20, mCategory)
                    .subscribe(new Consumer<List<SimpleNews>>() {
                        @Override
                        public void accept(List<SimpleNews> simpleNewses) throws Exception {
                            System.out.println(System.currentTimeMillis() - start + " | " + mCategory + " | " + simpleNewses.size());
                            mLoading = false;
                            mView.onSuccess(simpleNewses.size() == 0); // TODO check if load completed
                            // TODO onError
                            if (mPageNo == 1) mView.setNewsList(simpleNewses);
                            else mView.appendNewsList(simpleNewses);
                        }
                    });
        } else {
            Manager.I.recommend()
                    .subscribe(new Consumer<List<SimpleNews>>() {
                        @Override
                        public void accept(List<SimpleNews> simpleNewses) throws Exception {
                            System.out.println(System.currentTimeMillis() - start + " | " + mCategory);
                            mLoading = false;
                            if (mPageNo > 1 || simpleNewses.size() == 0) {
                                mView.onSuccess(true);
                                mView.appendNewsList(new ArrayList<>());
                            } else {
                                mView.onSuccess(false);
                                mView.setNewsList(simpleNewses);
                            }
                        }
                    });
        }
    }
}
