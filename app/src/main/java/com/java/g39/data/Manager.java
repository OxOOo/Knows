package com.java.g39.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.java.g39.BuildConfig;
import com.java.g39.R;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
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
    public static Manager I = null;

    /**
     * 创建单例，全局只能调用一次
     * @param context 上下文
     */
    public static void CreateI(Context context) {
        if (BuildConfig.DEBUG && I != null) {
            throw new AssertionError();
        }
        I = new Manager(context);
    }

    private FS fs;
    private FlowableTransformer<SimpleNews, SimpleNews> liftAllSimple;
    private FlowableTransformer<DetailNews, DetailNews> liftAllDetail;

    class FetchRead<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull T t) throws Exception {
            t.has_read = fs.hasRead(t.news_ID);
            return t;
        }
    }
    class FetchFavorite<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull T t) throws Exception {
            t.has_read = fs.hasRead(t.news_ID);
            return t;
        }
    }
    class FetchPicture<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull final T t) throws Exception {
            t.picture_url = Single.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String picture_url = null;

                    if (t.news_Pictures.trim().length() > 0) { // 新闻中的图片
                        String url = t.news_Pictures.trim().split(";")[0].split(" ")[0];
                        if (fs.downloadImage(url) != null) picture_url = url; // 如果第一个链接不可用，则从网络上选取
                    }
                    if (picture_url == null) { // 磁盘载入
                        picture_url = fs.fetchPictureUrl(t.news_ID);
                    }
                    if (picture_url == null) { // 搜索
                        DetailNews news = fs.fetchDetail(t.news_ID);
                        if (news == null) news = API.GetDetailNews(t.news_ID);
                        fs.insertDetail(news);

                        DetailNews.WordWithScore key = null;
                        for(DetailNews.WordWithScore k : news.Keywords) {
                            if (key == null || key.score < k.score) key = k;
                        }
                        if (key != null) picture_url = ImageSearch.search(key.word);
                    }

                    if (picture_url != null) {
                        fs.insertPictureUrl(t.news_ID, picture_url);
                    }
                    return picture_url;
                }
            }).subscribeOn(Schedulers.io());
            return t;
        }
    }

    private Manager(Context context) {
        this.fs = new FS(context);
        this.liftAllSimple = new FlowableTransformer<SimpleNews, SimpleNews>() {
            @Override
            public Publisher<SimpleNews> apply(@NonNull Flowable<SimpleNews> upstream) {
                return upstream
                        .map(new FetchRead<SimpleNews>())
                        .map(new FetchFavorite<SimpleNews>())
                        .map(new FetchPicture<SimpleNews>());
            }
        };
        this.liftAllDetail = new FlowableTransformer<DetailNews, DetailNews>() {
            @Override
            public Publisher<DetailNews> apply(@NonNull Flowable<DetailNews> upstream) {
                return upstream
                        .map(new Function<DetailNews, DetailNews>() {
                            @Override
                            public DetailNews apply(@NonNull DetailNews detailNews) throws Exception {
                                fs.insertDetail(detailNews);
                                return detailNews;
                            }
                        })
                        .map(new FetchRead<DetailNews>())
                        .map(new FetchFavorite<DetailNews>())
                        .map(new FetchPicture<DetailNews>());
            }
        };
    }

    /**
     * 获取新闻
     * @param pageNo
     * @param pageSize
     * @param category
     * @return
     */
    public Single<List<SimpleNews>> fetchSimpleNews(final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() throws Exception {
                return fs.fetchSimple(pageNo, pageSize, category);
            }
        }).flatMap(new Function<List<SimpleNews>, Publisher<SimpleNews>>() {
            @Override
            public Publisher<SimpleNews> apply(@NonNull List<SimpleNews> simpleNewses) throws Exception {
                if (simpleNewses.size() == pageSize) return Flowable.fromIterable(simpleNewses);
                return Flowable.fromIterable(API.GetSimpleNews(pageNo, pageSize, category));
            }
        }).map(new Function<SimpleNews, SimpleNews>() {
            @Override
            public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                fs.insertSimple(simpleNews, category);
                return simpleNews;
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取新闻
     * @param news_ID
     * @return
     */
    public Single<DetailNews> fetchDetailNews(final String news_ID) {
        return Flowable.fromCallable(new Callable<DetailNews>() {
            @Override
            public DetailNews call() throws Exception {
                DetailNews news = fs.fetchDetail(news_ID); // load from disk
                return news != null ? news : new DetailNews();
            }
        }).flatMap(new Function<DetailNews, Publisher<DetailNews>>() {
            @Override
            public Publisher<DetailNews> apply(@NonNull DetailNews detailNews) throws Exception {
                if (detailNews.news_ID != null) return Flowable.just(detailNews);
                return Flowable.just(API.GetDetailNews(news_ID)); // load from web
            }
        }).compose(this.liftAllDetail).firstOrError().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 搜索新闻
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @param category
     * @return
     */
    public Single<List<SimpleNews>> searchNews(final String keyword, final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() throws Exception {
                return API.SearchNews(keyword, pageNo, pageSize, category);
            }
        }).flatMap(new Function<List<SimpleNews>, Publisher<? extends SimpleNews>>() {
            @Override
            public Publisher<? extends SimpleNews> apply(@NonNull List<SimpleNews> simpleNewses) throws Exception {
                return Flowable.fromIterable(simpleNewses);
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 添加已读
     * @param news_ID
     */
    public void touchRead(final String news_ID) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                fs.insertRead(news_ID);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 添加收藏
     * @param news_ID
     */
    public void insertFavorite(final String news_ID) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                fs.insertFavorite(news_ID);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 取消收藏
     * @param news_ID
     */
    public void removeFavorite(final String news_ID) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                fs.removeFavorite(news_ID);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     *
     * @return 收藏列表
     */
    public Single<List<DetailNews>> favorites() {
        return Flowable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return fs.fetchFavorite();
            }
        }).flatMap(new Function<List<String>, Publisher<String>>() { // 展开
            @Override
            public Publisher<String> apply(@NonNull List<String> strings) throws Exception {
                return Flowable.fromIterable(strings);
            }
        }).flatMap(new Function<String, Publisher<DetailNews>>() { // 获取
            @Override
            public Publisher<DetailNews> apply(@NonNull String news_ID) throws Exception {
                DetailNews news = fs.fetchDetail(news_ID);
                if (news != null) return Flowable.just(news);
                return Flowable.just(API.GetDetailNews(news_ID));
            }
        }).compose(this.liftAllDetail).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Bitmap fetchBitmap(String news_Pictures) {
        news_Pictures = news_Pictures.trim();
        Bitmap picture = null;

        if (news_Pictures.length() > 0) {
            picture = fs.downloadImage(news_Pictures.split(";")[0].split(" ")[0]);
        }
//        if (picture == null) {
//            picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // default picture
//        }

        return picture;
    }
}
