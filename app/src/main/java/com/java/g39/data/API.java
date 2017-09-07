package com.java.g39.data;

import com.java.g39.data.SimpleNews;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by chenyu on 2017/9/7.
 */

public class API {
    private API() { }

    /**
     * 获取新闻，不设置subscribeOn
     * @param pageNo
     * @param pageSize
     * @param category -1表示不设置
     * @return 新闻列表，只需设置网络获取的字段
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize, int category)
    {
        return null;
    }

    /**
     * 获取新闻，不设置subscribeOn
     * @param pageNo
     * @param pageSize
     * @return 新闻列表，只需设置网络获取的字段
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize)
    {
        return GetSimpleNews(pageNo, pageSize, -1);
    }
}
