package com.java.g39.newslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;

import java.util.List;

/**
 * Created by equation on 9/7/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment implements NewsListContract.View {

    private NewsListContract.Presenter mPresenter;

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsListFragment newInstance() {
        NewsListFragment fragment = new NewsListFragment();
        NewsListPresenter presenter = new NewsListPresenter(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void setPresenter(NewsListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return null;
    }

    @Override
    public void start(Intent intent) {
    }

    @Override
    public void setNewsList(List<SimpleNews> list) {
    }

    @Override
    public void appendNewsList(List<SimpleNews> list) {
    }
}
