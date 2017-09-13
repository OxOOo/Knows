package com.java.g39.settings;

import android.os.Bundle;

import com.java.g39.BasePresenter;
import com.java.g39.BaseView;
import com.java.g39.data.Config;
import com.java.g39.data.SimpleNews;

import java.util.List;

/**
 * Created by equation on 9/12/17.
 */

public interface SettingsContract {

    interface View extends BaseView<Presenter> {

        void showNightMode(boolean is_night_mode);

        void showTextMode(boolean is_text_mode);

        void setAllCategories(List<Config.Category> list);

        void setAvailableCategories(List<Config.Category> list);

        /**
         * 弹窗
         * @param title 标题
         */
        void onShowToast(String title);

        /**
         * 弹窗
         * @param title 标题
         * @param message 消息
         */
        void onShowAlertDialog(String title,String message);
    }

    interface Presenter extends BasePresenter {

        void switchNightMode();

        void switchTextMode();

        void switchAvailableCategory(int idx);

        /**
         * 清除缓存
         */
        void cleanCache();

        /**
         * 检查更新
         */
        void checkUpdate();
    }
}
