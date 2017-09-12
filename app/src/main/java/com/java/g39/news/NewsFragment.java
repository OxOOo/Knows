package com.java.g39.news;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.g39.R;
import com.java.g39.data.Constant;
import com.java.g39.news.newslist.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻主页面
 * Created by equation on 9/8/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NewsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), Constant.CATEGORY_COUNT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_page, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        for (int i = 0; i < Constant.CATEGORY_COUNT; i++)
            mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < Constant.CATEGORY_COUNT; i++) {
            mTabLayout.getTabAt(i).setText(Constant.CATEGORYS[i]);
        }

        return view;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments = new ArrayList<Fragment>();

        public MyPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            for (int i = 0; i < size; i++)
                mFragments.add(null);
        }

        @Override
        public Fragment getItem(int position) {
            try {
                if (mFragments.get(position) == null)
                    mFragments.set(position, NewsListFragment.newInstance(position));
                return mFragments.get(position);
            } catch (Exception e) {
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return Constant.CATEGORY_COUNT;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
