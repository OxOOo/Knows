package com.java.g39.newslist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.java.g39.R;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class NewsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "onResume")
    }
}
