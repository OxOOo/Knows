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
}
