package com.java.g39.data;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.java.g39.BuildConfig;
import com.java.g39.R;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/8.
 * 所有数据相关操作的管理员，单例
 * 已设置subscribeOn(Schedulers.io())和observeOn(AndroidSchedulers.mainThread())
 * 网络优先
 */

public class Manager {
    public static Manager I = null;

    /**
     * 创建单例
     * @param context 上下文
     */
    public static synchronized void CreateI(Context context) {
        try {
            I = new Manager(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    private FS fs;
    private Config config;
    private AC_AutoMaton ac;
    private FlowableTransformer<SimpleNews, SimpleNews> liftAllSimple;
    private FlowableTransformer<DetailNews, DetailNews> liftAllDetail;

    private Manager(final Context context) throws IOException {
        this.fs = new FS(context);
        this.config = new Config(context);
        this.ac = new AC_AutoMaton();
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
                                if (detailNews == DetailNews.NULL) return detailNews;
                                fs.insertDetail(detailNews);
                                return detailNews;
                            }
                        })
                        .map(new FetchRead<DetailNews>())
                        .map(new FetchFavorite<DetailNews>())
                        .map(new FetchPicture<DetailNews>())
                        .map(new FetchLinks());
            }
        };
    }

    public Single<Boolean> waitForInit() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fs.waitForInit();
                for(String key: fs.getWordPV().keySet()) {
                    ac.add(key, getWordPV().get(key));
                }
                ac.fix();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Config getConfig() {
        return config;
    }

    Map<String, Integer> getWordPV() {
        return fs.getWordPV();
    }

    /**
     * 获取新闻,如果是由于关键词过滤掉了全部，则List<SimpleNews>包含DetailNews.NULL
     * @param pageNo
     * @param pageSize
     * @param category
     * @return
     */
    public Single<List<SimpleNews>> fetchSimpleNews(final int pageNo, final int pageSize, final int category) {
        return Flowable.fromCallable(new Callable<List<SimpleNews>>() {
            @Override
            public List<SimpleNews> call() throws Exception {
                try {
                    return API.GetSimpleNews(pageNo, pageSize, category);
                } catch(Exception e) {
                    return new ArrayList<SimpleNews>();
                }
            }
        }).flatMap(new Function<List<SimpleNews>, Publisher<SimpleNews>>() {
            @Override
            public Publisher<SimpleNews> apply(@NonNull List<SimpleNews> simpleNewses) throws Exception {
                if (simpleNewses.size() > 0) return Flowable.fromIterable(simpleNewses);
                return Flowable.fromIterable(fs.fetchSimple(pageNo, pageSize, category));
            }
        }).map(new Function<SimpleNews, SimpleNews>() {
            @Override
            public SimpleNews apply(@NonNull SimpleNews simpleNews) throws Exception {
                fs.insertSimple(simpleNews, category);
                return simpleNews;
            }
        }).compose(this.liftAllSimple).toList().map(new BlacklistFilter<SimpleNews>()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取新闻
     * @param news_ID
     * @return 如果成功，则返回新闻对象，否则返回DetailNews.NULL
     */
    public Single<DetailNews> fetchDetailNews(final String news_ID) {
        return Flowable.fromCallable(new Callable<DetailNews>() {
            @Override
            public DetailNews call() throws Exception {
                DetailNews news = fs.fetchDetail(news_ID); // load from disk
                return news != null ? news : DetailNews.NULL;
            }
        }).flatMap(new Function<DetailNews, Publisher<DetailNews>>() {
            @Override
            public Publisher<DetailNews> apply(@NonNull DetailNews detailNews) throws Exception {
                if (detailNews != DetailNews.NULL) return Flowable.just(detailNews);
                try {
                    return Flowable.just(API.GetDetailNews(news_ID)); // load from web
                } catch (Exception e) {
                    return Flowable.just(DetailNews.NULL);
                }
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
                try {
                    return API.SearchNews(keyword, pageNo, pageSize, category);
                } catch(Exception e) {
                    e.printStackTrace();
                    return new ArrayList<SimpleNews>();
                }
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
                DetailNews news = fs.fetchDetail(news_ID);
                try {
                    if (news == null) news = API.GetDetailNews(news_ID);
                } catch(Exception e) {

                }
                if (news != null)  fs.insertRead(news_ID, news);
                return new Object();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 添加收藏
     * @param news
     */
    public void insertFavorite(final DetailNews news) {
        Single.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                fs.insertFavorite(news.news_ID, news);
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
    public Single<List<SimpleNews>> favorites() {
        return Flowable.fromCallable(new Callable<List<DetailNews>>() {
            @Override
            public List<DetailNews> call() throws Exception {
                return fs.fetchFavorite();
            }
        }).flatMap(new Function<List<DetailNews>, Publisher<DetailNews>>() { // 展开
            @Override
            public Publisher<DetailNews> apply(@NonNull List<DetailNews> news) throws Exception {
                return Flowable.fromIterable(news);
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return 推荐
     */
    public Single<List<SimpleNews>> recommend() {
        final int pickSize = 50;
        final int topSize = 200;

        return Flowable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                List<DetailNews> all = fs.fetchAllFromSampleNotRead();
                List<DetailNews> read = fs.fetchRead();
                List<DetailNews> favorite = fs.fetchFavorite();
                List<DetailNews> rec = RecSystem.getInstance().recommendSort(all, read, favorite);

                Set<Integer> index = new TreeSet<Integer>();
                Random r = new Random();
                for(int i = 0; i < pickSize; i ++) {
                    if (index.size() == rec.size()) continue;
                    int x = r.nextInt(topSize);
                    while(x >= rec.size() || index.contains(x)) x = r.nextInt(topSize);
                    index.add(x);
                }
                List<String> ids = new ArrayList<String>();
                for(int x: index) {
                    ids.add(rec.get(x).news_ID);
                }
                return ids;
            }
        }).flatMap(new Function<List<String>, Publisher<String>>() {
            @Override
            public Publisher<String> apply(@NonNull List<String> strings) throws Exception {
                return Flowable.fromIterable(strings);
            }
        }).map(new Function<String, SimpleNews>() {
            @Override
            public SimpleNews apply(@NonNull String s) throws Exception {
                return fs.fetchDetailFromSample(s);
            }
        }).compose(this.liftAllSimple).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 清空数据库缓存
     * @return 是否成功
     */
    public Single<Boolean> clean() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                fs.dropTables();
                fs.createTables();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 分享
     * @param activity 调用者
     * @param title 标题
     * @param text 文本内容
     * @param url 分享链接
     * @param imgUrl 图片链接
     */
    public static void shareNews(Activity activity, String title, String text, String url, String imgUrl) {
        API.ShareNews(activity, title, text, url, imgUrl);
    }

    // ========================================================
    private class FetchRead<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull T t) throws Exception {
            if (t == DetailNews.NULL) return t;

            t.has_read = fs.hasRead(t.news_ID);
            return t;
        }
    }
    private class FetchFavorite<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull T t) throws Exception {
            if (t == DetailNews.NULL) return t;

            t.is_favorite = fs.isFavorite(t.news_ID);
            return t;
        }
    }
    private class FetchPicture<T extends SimpleNews> implements Function<T, T> {
        @Override
        public T apply(@NonNull final T t) throws Exception {
            if (t == DetailNews.NULL) return t;

            t.single_picture_url = Single.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    if (config.isTextMode()) return ""; // 文字模式

                    String picture_url = fs.fetchPictureUrl(t.news_ID);

                    if (picture_url == null) { // 搜索
                        DetailNews news = fs.fetchDetail(t.news_ID);
                        try {
                            if (news == null) news = API.GetDetailNews(t.news_ID);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if (news != null) {
                            fs.insertDetail(news);

                            DetailNews.WordWithScore key = null;
                            for(DetailNews.WordWithScore k : news.Keywords) {
                                if (key == null || key.score < k.score) key = k;
                            }
                            try {
                                if (key != null) picture_url = ImageSearch.search(key.word);
                            } catch(Exception e) {
                                Log.d("DEBUG", "ERROR ON ImageSearch");
                            }
                        }
                    }

                    if (picture_url != null) {
                        fs.insertPictureUrl(t.news_ID, picture_url);
                    } else Log.e("ERROR", t.news_ID);

                    if (picture_url == null) picture_url = "";
                    t.picture_url = picture_url;
                    return picture_url;
                }
            }).subscribeOn(Schedulers.io());
            return t;
        }
    }
    private class FetchLinks implements Function<DetailNews, DetailNews> {

        @Override
        public DetailNews apply(@NonNull final DetailNews detailNews) throws Exception {
            if (detailNews == DetailNews.NULL) return detailNews;

            detailNews.links = Flowable.just(0)
                    .flatMap(new Function<Integer, Publisher<String>>() {
                        @Override
                        public Publisher<String> apply(@NonNull Integer integer) throws Exception {
                            return Flowable.fromIterable(detailNews.getKeywordHyperlink(ac));
                        }
                    })
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(@NonNull String word) throws Exception {
                            try {
                                String link = "https://baike.baidu.com/item/"+ URLEncoder.encode(word, "UTF-8");
                                Boolean value = fs.getLinkValue(word);
                                if (value == null) value = API.TestBaikeConnection(link);
                                if (value != null) fs.setLinkValue(word, value);
                                return value == null ? false : value;
                            } catch(Exception e) {
                                return false;
                            }
                        }
                    })
                    .toList()
                    .map(new Function<List<String>, Map<String, String>>() {
                        @Override
                        public Map<String, String> apply(@NonNull List<String> entries) throws Exception {
                            Map<String, String> rst = new HashMap<String, String>();
                            for(String e: entries)
                                rst.put(e, "https://baike.baidu.com/item/"+ URLEncoder.encode(e, "UTF-8"));
                            return rst;
                        }
                    }).subscribeOn(Schedulers.io());

            return detailNews;
        }
    }
    private class BlacklistFilter<T extends SimpleNews> implements Function<List<T>, List<T>> {

        @Override
        public List<T> apply(@NonNull List<T> Ts) throws Exception {
            List<T> new_Ts = new ArrayList<T>();
            for(T t: Ts) {
                boolean flag = true;
                for(String item: config.getBlacklist())
                    if (t.news_Title.toLowerCase().contains(item)) {
                        flag = false;
                        break;
                    }
                if (flag) new_Ts.add(t);
            }
            if (new_Ts.size() == 0 && Ts.size() != 0) {
                new_Ts.add((T)DetailNews.NULL);
            }
            return new_Ts;
        }
    }
}
