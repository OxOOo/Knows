package com.java.g39.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

import com.java.g39.R;
import com.java.g39.data.Config;
import com.java.g39.data.Constant;
import com.java.g39.data.Manager;

import java.util.List;

/**
 * 设置页面
 */
public class SettingsFragment extends Fragment implements SettingsContract.View {

    private SettingsContract.Presenter mPresenter;
    private Switch mNightSwitch;
    private Switch mTextSwitch;
    private Button mAddButton;

    private RecyclerView mTagsView;
    private TagsAdapter mTagsAdapter;

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
        mTagsAdapter = new TagsAdapter();
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

        view.findViewById(R.id.button_clear).setOnClickListener((View v) -> mPresenter.cleanCache());
        view.findViewById(R.id.button_update).setOnClickListener((View v) -> mPresenter.checkUpdate());

        mNightSwitch = (Switch) view.findViewById(R.id.switch_night);
        mTextSwitch = (Switch) view.findViewById(R.id.switch_text);
        mAddButton = (Button) view.findViewById(R.id.button_add_tag);
        mNightSwitch.setOnClickListener((View v) -> mPresenter.switchNightMode());
        mTextSwitch.setOnClickListener((View v) -> mPresenter.switchTextMode());
        mAddButton.setOnClickListener((View v) -> {
            List<Config.Category> list = Manager.I.getConfig().unavailableCategories();
            if (list.isEmpty()) return;
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++)
                array[i] = list.get(i).title;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("请选择要添加的首页标签").setNegativeButton("取消", null);
            builder.setItems(array, (DialogInterface dialog, int which) -> {
                mTagsAdapter.addTag(list.get(which).idx);
            });
            builder.create().show();
        });

        mAddButton.setEnabled(mTagsAdapter.getItemCount() < Constant.CATEGORY_COUNT);
        mTagsAdapter.setOnTagsCountChangeListener((int count) -> mAddButton.setEnabled(count < Constant.CATEGORY_COUNT));

        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER)
                .withLastRow(true)
                .build();
        mTagsView = (RecyclerView) view.findViewById(R.id.tags_view);
        mTagsView.setLayoutManager(chipsLayoutManager);
        mTagsView.setItemAnimator(new DefaultItemAnimator());
        mTagsView.setAdapter(mTagsAdapter);

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
    public void onShowToast(String title) {
        Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowAlertDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    @Override
    public void showNightMode(boolean is_night_mode) {
        mNightSwitch.setChecked(is_night_mode);
    }

    @Override
    public void showTextMode(boolean is_text_mode) {
        mTextSwitch.setChecked(is_text_mode);
    }
}
