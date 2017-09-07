/**
 * FIXME @equation314
 * Deprecated
 * Use NewsListFragment
 */

package com.java.g39.newslist;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.java.g39.R;
import com.java.g39.data.FS;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NewsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView image_view = (ImageView) findViewById(R.id.imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Flowable.just("Hello RxJava").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("DEBUG", s);
            }
        });

        Flowable.just("http://himg2.huanqiu.com/attachment2010/2016/0912/13/16/20160912011630240.png")
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull String s) throws Exception {
                        return FS.DownloadImage(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        image_view.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "onResume");
    }
}
