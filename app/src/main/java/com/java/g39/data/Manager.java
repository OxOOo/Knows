package com.java.g39.data;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.java.g39.R;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/8.
 * 所有数据相关操作的管理员，单例
 * 已设置subscribeOn(Schedulers.io())和observeOn(AndroidSchedulers.mainThread())
 */

public class Manager {
    public static Manager I = new Manager();

    private Manager() {

    }

    public Single<List<SimpleNews>> fetchSimpleNews(final int pageNo, final int pageSize, final int category, final Context context) {
        return API.GetSimpleNews(pageNo, pageSize, category)
                .map(new Function<SimpleNews, SimpleNews>() {
                    @Override
                    public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                        simpleNews.has_read = false;
                        simpleNews.from_disk = false;
                        if (simpleNews.news_Pictures.length() > 0) {
                            simpleNews.picture = FS.DownloadImage(simpleNews.news_Pictures.split(";")[0]);
                        } else {
                            simpleNews.picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // default picture
                        }
                        return simpleNews;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<DetailNews> fetchDetailNews(final String news_ID, final Context context) {
        return API.GetDetailNews(news_ID)
                .map(new Function<DetailNews, DetailNews>() {
                    @Override
                    public DetailNews apply(@NonNull DetailNews detailNews) throws Exception {
                        detailNews.has_read = false;
                        detailNews.from_disk = false;
                        if (detailNews.news_Pictures.length() > 0) {
                            detailNews.picture = FS.DownloadImage(detailNews.news_Pictures.split(";")[0]);
                        } else {
                            detailNews.picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // default picture
                        }
                        return detailNews;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
