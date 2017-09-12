package com.java.g39.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;

import com.java.g39.R;
import com.java.g39.data.Config;
import com.java.g39.data.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SettingsFragment extends Fragment implements SettingsContract.View {

    private SettingsContract.Presenter mPresenter;
    private CheckBox mNightModeBox;
    private CheckBox mTextModeBox;

    private GridLayout mCategoriesLayout;
    private List<Config.Category> mAllList;
    private List<CheckBox> mBoxes;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SettingsPresenter(this);
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mNightModeBox = (CheckBox)view.findViewById(R.id.nightModeBox);
        mTextModeBox = (CheckBox)view.findViewById(R.id.textModeBox);
        mNightModeBox.setOnClickListener((View v) -> {
            mPresenter.switchNightMode();
        });
        mTextModeBox.setOnClickListener((View v) -> {
            mPresenter.switchTextMode();
        });

        ((Button)view.findViewById(R.id.cleanBtn)).setOnClickListener((View v) -> {
            mPresenter.clean();
        });

        mCategoriesLayout = (GridLayout)view.findViewById(R.id.categoriesLayout);
        mCategoriesLayout.setColumnCount(4);

        return view;
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    @Override
    public void showNightMode(boolean is_night_mode) {
        mNightModeBox.setChecked(is_night_mode);
    }

    @Override
    public void showTextMode(boolean is_text_mode) {
        mTextModeBox.setChecked(is_text_mode);
    }

    @Override
    public void setAllCategories(List<Config.Category> list) {
        mAllList = list;
        mBoxes = new ArrayList<>();
        mCategoriesLayout.removeAllViews();

        for(Config.Category c: list) {
            CheckBox box = new CheckBox(getContext());
            box.setText(c.title);
            mBoxes.add(box);
            mCategoriesLayout.addView(box);

            box.setOnClickListener((View v) -> {
                for(int i = 0; i < mAllList.size(); i ++)
                    if (v == mBoxes.get(i))
                        mPresenter.switchAvailableCategory(mAllList.get(i).idx);
            });
        }
    }

    @Override
    public void setAvailableCategories(List<Config.Category> list) {
        for(int i = 0; i < mAllList.size(); i ++)
        {
            boolean checked = false;
            for(Config.Category c: list)
                if (c.idx == mAllList.get(i).idx)
                    checked = true;
            mBoxes.get(i).setChecked(checked);
        }
    }
}
