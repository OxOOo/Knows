package com.java.g39.news.newsdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.g39.R;
import com.java.g39.data.DetailNews;
import com.java.g39.data.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailContract.View {

    public static final String NEWS_ID = "NEWS_ID";
    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_PICTURE_URL = "NEWS_PICTURE_URL";
    public static final String NEWS_IS_FAVORITED = "NEWS_IS_FAVORITED";

    private NewsDetailContract.Presenter mPresenter;
    private DetailNews mNews;

    private TextView mTag, mDetail, mContent;
    private ImageView mImage;
    private FloatingActionButton mFab;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNews != null) {
                    if (mNews.is_favorite) {
                        mPresenter.unFavorite(mNews);
                        mFab.setSelected(false);
                    } else {
                        mPresenter.favorite(mNews);
                        mFab.setSelected(true);
                    }
                }
            }
        });

        String news_ID = getIntent().getStringExtra(NEWS_ID);
        String news_Title = getIntent().getStringExtra(NEWS_TITLE);
        String news_picture_url = getIntent().getStringExtra(NEWS_PICTURE_URL);
        boolean news_is_favorited = getIntent().getBooleanExtra(NEWS_IS_FAVORITED, false);

        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(news_Title);

        mTag = (TextView) findViewById(R.id.text_tag);
        mDetail = (TextView) findViewById(R.id.text_detail);
        mContent = (TextView) findViewById(R.id.text_content);
        mImage = (ImageView) findViewById(R.id.image_view);

        mFab.setSelected(news_is_favorited);
        if (news_picture_url != null) {
            ImageLoader.displayImage(news_picture_url, mImage);
        }

        mPresenter = new NewsDetailPresenter(this, news_ID);
        mPresenter.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        mFab.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    public void setPresenter(NewsDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void setNewsDetail(DetailNews news) {
        mNews = news;
        mTag.setText(news.newsClassTag);
        mDetail.setText((news.news_Author.isEmpty() ? news.news_Source : news.news_Author) + "　" + news.news_Time);
        String content = news.news_Content.trim();
        mContent.setText(TextUtils.join("\n\n　　", content.split(" 　　")));
        mFab.setClickable(true);
        mFab.setSelected(news.is_favorite);
        news.single_picture_url
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        ImageLoader.displayImage(s, mImage);
                    }
                });
    }
}
