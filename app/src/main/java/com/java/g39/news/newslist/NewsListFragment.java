package com.java.g39.news.newslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表，带分类
 * Created by equation on 9/7/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewsListFragment extends Fragment implements NewsListContract.View {

    private NewsListContract.Presenter mPresenter;
    private int mCategory;

    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category 新闻分类 code
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsListFragment newInstance(int category) {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        args.putInt("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getInt("category");
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new NewsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new NewsListPresenter(this, mCategory);
        return view;
    }

    @Override
    public void setPresenter(NewsListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void setNewsList(List<SimpleNews> list) {
        mAdapter.setData(list);
    }

    @Override
    public void appendNewsList(List<SimpleNews> list) {
        mAdapter.appendData(list);
    }
}
