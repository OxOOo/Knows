package com.java.g39.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.java.g39.R;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/8.
 * 所有数据相关操作的管理员，单例
 * 已设置subscribeOn(Schedulers.io())和observeOn(AndroidSchedulers.mainThread())
 */

public class Manager {
    public static Manager I = null;

    /**
     * 创建单例，全局只能调用一次
     * @param context 上下文
     */
    public static void CreateI(Context context) {
        assert (I == null);
        I = new Manager(context);
    }

    private Context context;
    private FS fs;

    private Manager(Context context) {
        this.context = context;
        this.fs = new FS(context);
    }

    public Single<List<SimpleNews>> fetchSimpleNews(final int pageNo, final int pageSize, final int category) {
        return API.GetSimpleNews(pageNo, pageSize, category)
                .map(new Function<SimpleNews, SimpleNews>() {
                    @Override
                    public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                        fs.insertSimple(simpleNews, category);
                        return simpleNews;
                    }
                })
                .map(new Function<SimpleNews, SimpleNews>() {
                    @Override
                    public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                        simpleNews.has_read = false;
                        simpleNews.from_disk = false;
                        simpleNews.picture_url = null;
                        if (simpleNews.news_Pictures.trim().length() > 0) {
                            simpleNews.picture_url = simpleNews.news_Pictures.trim().split(";")[0].split(" ")[0];
                        }
                        return simpleNews;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<DetailNews> fetchDetailNews(final String news_ID) {
        return API.GetDetailNews(news_ID)
                .map(new Function<DetailNews, DetailNews>() {
                    @Override
                    public DetailNews apply(@NonNull DetailNews detailNews) throws Exception {
                        detailNews.has_read = false;
                        detailNews.from_disk = false;
                        detailNews.picture_url = null;
                        if (detailNews.news_Pictures.trim().length() > 0) {
                            detailNews.picture_url = detailNews.news_Pictures.trim().split(";")[0].split(" ")[0];
                        }
                        return detailNews;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SimpleNews>> searchNews(final String keyword, final int pageNo, final int pageSize, final int category) {
        return API.SearchNews(keyword, pageNo, pageSize, category)
                .map(new Function<SimpleNews, SimpleNews>() {
                    @Override
                    public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                        simpleNews.has_read = false;
                        simpleNews.from_disk = false;
                        simpleNews.picture_url = null;
                        if (simpleNews.news_Pictures.trim().length() > 0) {
                            simpleNews.picture_url = simpleNews.news_Pictures.trim().split(";")[0].split(" ")[0];
                        }
                        return simpleNews;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Bitmap fetchBitmap(String news_Pictures) {
        news_Pictures = news_Pictures.trim();
        Bitmap picture = null;

        if (news_Pictures.length() > 0) {
            picture = fs.downloadImage(news_Pictures.split(";")[0].split(" ")[0]);
        }
        if (picture == null) {
            picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // default picture
        }

        return picture;
    }
}
