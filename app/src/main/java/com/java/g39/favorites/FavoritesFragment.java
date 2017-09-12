package com.java.g39.favorites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;
import com.java.g39.news.newslist.NewsAdapter;

import java.util.List;

/**
 * 收藏列表
 * Created by equation on 9/12/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class FavoritesFragment extends Fragment implements FavoritesContract.View {

    private FavoritesContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mLastClickPosition = -1;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;

        mAdapter = new NewsAdapter(getContext());
        mAdapter.setFooterVisible(false);
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            SimpleNews news = mAdapter.getNews(position);
            View transitionView = itemView.findViewById(R.id.image_view);
            this.mLastClickPosition = position;

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            transitionView, getString(R.string.transition_news_img));

            this.mPresenter.openNewsDetailUI(news, options.toBundle());
        });

        mPresenter = new FavoritesPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            boolean isFavorited = data.getBooleanExtra("IS_FAVORITED", true);
            if (!isFavorited && mLastClickPosition >= 0)
                mAdapter.removeItem(mLastClickPosition);
        }
    }

    @Override
    public void setPresenter(FavoritesContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setFavorites(List<SimpleNews> list) {
        mAdapter.setData(list);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivityForResult(intent, 0, options);
    }
}
