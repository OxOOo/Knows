package com.java.g39.settings;

import android.util.Log;
import android.widget.Toast;

import com.java.g39.data.Config;
import com.java.g39.data.ImageLoader;
import com.java.g39.data.Manager;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by chenyu on 2017/9/12.
 */

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mView;
    private Config mConfig;

    SettingsPresenter(SettingsContract.View view) {
        mView = view;
        mConfig = Manager.I.getConfig();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mView.showNightMode(mConfig.isNightMode());
        mView.showTextMode(mConfig.isTextMode());
    }

    @Override
    public void unsubscribe() {
        // nothing
    }

    @Override
    public void switchNightMode() {
        mConfig.setNightMode(!mConfig.isNightMode());
    }

    @Override
    public void switchTextMode() {
        mConfig.setTextMode(!mConfig.isTextMode());
    }

    @Override
    public void cleanCache() {
        ImageLoader.clearDiskCache();
        mView.onShowToast("已清除图片缓存");
        Manager.I.clean()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) mView.onShowToast("已清除数据库记录");
                        else mView.onShowToast("清除数据库记录失败");
                    }
                });
    }

    @Override
    public void checkUpdate() {
        mView.onShowAlertDialog("您已经是最新版", "当前版本：V1.0.0");
    }

    @Override
    public List<Config.Category> getTags() {
        return mConfig.availableCategories(true);
    }

    @Override
    public void addTag(Config.Category tag) {
        if (mConfig.addCategory(tag))
            mView.onAddTag(tag);
    }

    @Override
    public void removeTag(Config.Category tag, int position) {
        if (mConfig.removeCategory(tag))
            mView.onRemoveTag(tag, position);
    }

    @Override
    public List<String> getBlacklist() {
        return mConfig.getBlacklist();
    }

    @Override
    public void addKeyword(String keyword) {
        if (mConfig.insertBlacklist(keyword))
            mView.onAddKeyword(keyword);
        else
            mView.onShowToast("该关键词已存在");
    }

    @Override
    public void removeKeyword(String keyword, int position) {
        if (mConfig.removeBlacklist(keyword))
            mView.onRemoveKeyword(keyword, position);
    }
}
