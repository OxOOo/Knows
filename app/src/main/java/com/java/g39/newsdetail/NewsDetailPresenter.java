package com.java.g39.newsdetail;

import com.java.g39.data.DetailNews;
import com.java.g39.data.Manager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    }

    @Override
    public void subscribe() {
        Manager.I.fetchDetailNews(mNews_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        mView.setNewsDetail(detailNews);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        // nothing
    }
}
