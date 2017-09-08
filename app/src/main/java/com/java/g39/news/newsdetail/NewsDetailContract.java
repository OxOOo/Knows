package com.java.g39.news.newsdetail;

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
        public void setNewsDetail(DetailNews news);
    }

    interface Presenter extends BasePresenter {

    }
}
