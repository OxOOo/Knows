package com.java.g39.news.newsdetail;

import android.util.Log;
import android.widget.Toast;

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
        Manager.I.touchRead(mNews_ID);
        Manager.I.fetchDetailNews(mNews_ID)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        if (detailNews == DetailNews.NULL) {
                            Toast.makeText(mView.context(), "无法获取新闻详情", Toast.LENGTH_LONG).show();
                        } else {
                            mView.setNewsDetail(detailNews);
                        }
                    }
                });
    }

    @Override
    public void favorite(DetailNews news) {
        news.is_favorite = true;
        Manager.I.insertFavorite(news);
//        Manager.I.favorites()
//                .subscribe(new Consumer<List<DetailNews>>() {
//                    @Override
//                    public void accept(List<DetailNews> detailNewses) throws Exception {
//                        for (DetailNews news : detailNewses) {
//                            System.out.println("favorites:" + news.news_Title);
//                        }
//                    }
//                });
    }

    @Override
    public void unFavorite(DetailNews news) {
        news.is_favorite = false;
        Manager.I.removeFavorite(news.news_ID);
    }

    @Override
    public void unsubscribe() {
        // nothing
    }
}
