package com.java.g39.settings;

import android.util.Log;
import android.widget.Toast;

import com.java.g39.data.Config;
import com.java.g39.data.ImageLoader;
import com.java.g39.data.Manager;

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
        mView.setAllCategories(mConfig.allCategories());
        mView.setAvailableCategories(mConfig.availableCategories());
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
    public void switchAvailableCategory(int idx) {
        mConfig.switchAvailable(idx);
    }

    @Override
    public void clean() {
        ImageLoader.clearDiskCache();
        Toast.makeText(mView.context(), "已清除图片缓存", Toast.LENGTH_SHORT).show();
        Manager.I.clean()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) Toast.makeText(mView.context(), "已清除数据库记录", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(mView.context(), "清除数据库记录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
