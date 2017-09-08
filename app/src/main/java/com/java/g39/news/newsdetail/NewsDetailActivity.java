package com.java.g39.news.newsdetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.java.g39.R;
import com.java.g39.data.DetailNews;
import com.java.g39.data.Manager;

import org.w3c.dom.Text;

import java.util.List;

import io.reactivex.functions.Consumer;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String NEWS_ID = "NEWS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the requested news id
        String news_ID = getIntent().getStringExtra(NEWS_ID);
        TextView text = (TextView)findViewById(R.id.textViewDetail);
        text.setText(news_ID);
        Manager.I.touchRead(news_ID);
        Manager.I.insertFavorite(news_ID);
        Manager.I.fetchDetailNews(news_ID)
                .subscribe(new Consumer<DetailNews>() {
                    @Override
                    public void accept(DetailNews detailNews) throws Exception {
                        System.out.println(detailNews.news_Content);
                        System.out.println(detailNews.has_read);
                        System.out.println(detailNews.is_favorite);
                    }
                });
        Manager.I.favorites()
                .subscribe(new Consumer<List<DetailNews>>() {
                    @Override
                    public void accept(List<DetailNews> detailNewses) throws Exception {
                        for(DetailNews news: detailNewses) {
                            System.out.println("favorites:" + news.news_Title);
                        }
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
