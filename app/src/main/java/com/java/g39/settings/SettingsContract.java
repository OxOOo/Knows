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

        /**
         * 添加首页标签
         * @param tag 分类标签
         */
        void onAddTag(Config.Category tag);

        /**
         * 删除首页标签
         * @param tag 分类标签
         * @param position 位置
         */
        void onRemoveTag(Config.Category tag, int position);

        /**
         * 删除屏蔽关键词
         * @param keyword 关键词
         */
        void onAddKeyword(String keyword);

        /**
         * 添加屏蔽关键词
         * @param keyword 关键词
         * @param position 位置
         */
        void onRemoveKeyword(String keyword, int position);

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

        /**
         * 清除缓存
         */
        void cleanCache();

        /**
         * 检查更新
         */
        void checkUpdate();

        /**
         * 获取首页标签列表
         * @return 分类列表
         */
        List<Config.Category> getTags();

        /**
         * 添加首页标签
         * @param tag 分类标签
         */
        void addTag(Config.Category tag);

        /**
         * 删除首页标签
         * @param tag 分类标签
         * @param position 位置
         */
        void removeTag(Config.Category tag,int position);

        /**
         * 获取屏蔽的关键词列表
         * @return 关键词列表
         */
        List<String> getBlacklist();

        /**
         * 删除屏蔽关键词
         * @param keyword 关键词
         */
        void addKeyword(String keyword);

        /**
         * 添加屏蔽关键词
         * @param keyword 关键词
         * @param position 位置
         */
        void removeKeyword(String keyword, int position);
    }
}
