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
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @param category 分类，-1表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize, int category)
    {
        // FIXME
        return null;
    }

    /**
     * 获取最近的新闻，不设置subscribeOn，只设置网络获取的字段
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> GetSimpleNews(int pageNo, int pageSize)
    {
        return GetSimpleNews(pageNo, pageSize, -1);
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @param category 分类，-1表示不设置
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(String keyword, int pageNo, int pageSize, int category)
    {
        // FIXME
        return null;
    }

    /**
     * 搜索新闻，不设置subscribeOn，只设置网络获取的字段
     * @param keyword 关键字
     * @param pageNo 页码
     * @param pageSize 每页新闻数量
     * @return 新闻列表
     */
    public static Flowable<SimpleNews> SearchNews(String keyword, int pageNo, int pageSize)
    {
        return SearchNews(keyword, pageNo, pageSize, -1);
    }

    /**
     * 获取新闻详情，不设置subscribeOn，只设置网络获取的字段
     * @param newsId ID
     * @return 新闻详情
     */
    public static Flowable<DetailNews> GetDetailNews(String newsId)
    {
        // FIXME
        return null;
    }
}
