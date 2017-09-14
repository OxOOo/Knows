package com.java.g39.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private Button mAddTagButton, mAddKeywordButton;

    private RecyclerView mTagsView, mBlacklistView;
    private ChipsAdapter mTagsAdapter, mBlacklistAdapter;

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
        mTagsAdapter = new ChipsAdapter<Config.Category>(mPresenter.getTags(), 0) {
            @Override
            String getChipsTitle(final Config.Category chip) {
                return chip.title;
            }
        };
        mBlacklistAdapter = new ChipsAdapter<String>(mPresenter.getBlacklist()) {
            @Override
            String getChipsTitle(final String chip) {
                return chip;
            }
        };
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
        mAddTagButton = (Button) view.findViewById(R.id.button_add_tag);
        mAddKeywordButton = (Button) view.findViewById(R.id.button_add_keyword);
        mNightSwitch.setOnClickListener((View v) -> mPresenter.switchNightMode());
        mTextSwitch.setOnClickListener((View v) -> mPresenter.switchTextMode());
        mAddTagButton.setOnClickListener((View v) -> {
            List<Config.Category> list = Manager.I.getConfig().unavailableCategories();
            if (list.isEmpty()) return;
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++)
                array[i] = list.get(i).title;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("请选择要添加的首页标签").setNegativeButton("取消", null);
            builder.setItems(array, (DialogInterface dialog, int which) -> mPresenter.addTag(list.get(which)));
            builder.create().show();
        });
        mAddKeywordButton.setOnClickListener((View v) -> {
            EditText input = new EditText(getContext());
            input.setSingleLine(true);
            input.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog dialog = builder.setTitle("请输入要屏蔽的关键词")
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", null)
                    .setView(input).create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View) -> {
                final String str = input.getText().toString().trim().toLowerCase();
                final String regex = "^[a-z0-9\u4e00-\u9fa5]+$";

                if (str.isEmpty())
                    onShowToast("关键词不能为空");
                else if (!str.matches(regex))
                    onShowToast("关键词只能包含汉字、字母和数字");
                else {
                    mPresenter.addKeyword(str);
                    dialog.dismiss();
                }
            });
        });

        mAddTagButton.setEnabled(mTagsAdapter.getItemCount() < Constant.CATEGORY_COUNT);
        mTagsAdapter.setOnTagsCountChangeListener((int count) -> mAddTagButton.setEnabled(count < Constant.CATEGORY_COUNT));
        mTagsAdapter.setOnRemoveChipListener((View v, int position) -> {
            Config.Category tag = (Config.Category) mTagsAdapter.getChip(position);
            mPresenter.removeTag(tag, position);
        });
        mBlacklistAdapter.setOnRemoveChipListener((View v, int position) -> {
            String keyword = (String) mBlacklistAdapter.getChip(position);
            mPresenter.removeKeyword(keyword, position);
        });

        ChipsLayoutManager tagsLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER)
                .withLastRow(true)
                .build();
        mTagsView = (RecyclerView) view.findViewById(R.id.tags_view);
        mTagsView.setLayoutManager(tagsLayoutManager);
        mTagsView.setItemAnimator(new DefaultItemAnimator());
        mTagsView.setAdapter(mTagsAdapter);

        ChipsLayoutManager blacklistLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER)
                .withLastRow(true)
                .build();
        mBlacklistView = (RecyclerView) view.findViewById(R.id.blacklist_view);
        mBlacklistView.setLayoutManager(blacklistLayoutManager);
        mBlacklistView.setItemAnimator(new DefaultItemAnimator());
        mBlacklistView.setAdapter(mBlacklistAdapter);

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
    public void onAddTag(Config.Category tag) {
        mTagsAdapter.addChip(tag);
    }

    @Override
    public void onRemoveTag(Config.Category tag, int position) {
        mTagsAdapter.removeChip(position);
    }

    @Override
    public void onAddKeyword(String keyword) {
        mBlacklistAdapter.addChip(keyword);
    }

    @Override
    public void onRemoveKeyword(String keyword, int position) {
        mBlacklistAdapter.removeChip(position);
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
