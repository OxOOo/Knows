package com.java.g39.news.newsdetail;

import android.app.Activity;

import com.java.g39.BasePresenter;
import com.java.g39.BaseView;
import com.java.g39.data.DetailNews;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface NewsDetailContract {

    interface View extends BaseView<NewsDetailContract.Presenter> {

        /**
         * 设置新闻，只设置
         * @param news 新闻
         */
        void setNewsDetail(DetailNews news);

        /**
         * 弹窗
         * @param title 标题
         */
        void onShowToast(String title);

        /**
         * 开始加载
         */
        void onStartLoading();

        /**
         * 获取新闻详情失败
         */
        void onError();

        /**
         * 设置图片可见性
         * @param visible 图片是否可见
         */
        void setImageVisible(boolean visible);
    }

    interface Presenter extends BasePresenter {

        /**
         * 收藏
         * @param news 新闻
         */
        void favorite(DetailNews news);

        /**
         * 取消收藏
         * @param news 新闻
         */
        void unFavorite(DetailNews news);

        /**
         * 分享
         * @param activity 调用者
         * @param news 新闻
         */
        void shareNews(Activity activity, DetailNews news);
    }
}
