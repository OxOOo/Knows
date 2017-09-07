package com.java.g39.newslist;

import android.content.Context;

import com.java.g39.BasePresenter;
import com.java.g39.BaseView;
import com.java.g39.data.SimpleNews;

import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface NewsListContract {

    interface View extends BaseView<Presenter> {

        /**
         * 清空当前UI，并填充新闻，用做初始化
         * 可能会调用多次
         * @param list 新闻列表
         */
        void setNewsList(List<SimpleNews> list);

        /**
         * 添加新闻到当前UI的后面
         * @param list 新闻列表
         */
        void appendNewsList(List<SimpleNews> list);
    }

    interface Presenter extends BasePresenter {

        /**
         * 新闻列表翻到了最底下，需要更多数据
         */
        void requireMoreNews();

        /**
         * 上拉刷新，重新获取最新的新闻
         */
        void refreshNews();

        /**
         * 打开新闻详情
         * @param news 被打开的新闻
         */
        void openNewsDetailUI(SimpleNews news);
    }
}
