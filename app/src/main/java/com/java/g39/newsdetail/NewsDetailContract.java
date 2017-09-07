package com.java.g39.newsdetail;

import com.java.g39.BasePresenter;
import com.java.g39.BaseView;
import com.java.g39.data.DetailNews;

import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface NewsDetailContract {

    interface View extends BaseView<NewsDetailContract.Presenter> {

        /**
         * 设置新闻
         * @param news 新闻
         */
        public void setNewsDetail(DetailNews news);
    }

    interface Presenter extends BasePresenter {

    }
}
