package com.java.g39.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenyu on 2017/9/12.
 * 配置相关
 */

public class Config {
    public interface NightModeChangeListener {
        void onChange();
    }
    public static class Category {
        public String title;
        public int idx;
        Category(String title, int idx) { this.title = title; this.idx = idx; }

        public static Category getRecommentCategory() {
            return new Category(Constant.CATEGORYS[0], 0);
        }
    }

    private String path;

    private Scheduler save_thread = Schedulers.newThread();
    private NightModeChangeListener mode_listener = null;
    private boolean night_mode; // 夜间模式
    private boolean text_mode; // 无图模式/文字模式
    private List<Integer> available_categories;
    private List<String> blacklist;

    Config(Context context) {
        path = context.getFilesDir().getPath() + "/config.json";
        loadConfig();
    }

    public void setNightModeChangeListener(NightModeChangeListener listener) {
        this.mode_listener = listener;
    }

    public boolean isNightMode() {
        return night_mode;
    }
    public void setNightMode(boolean is_night_mode) {
        night_mode = is_night_mode;
        saveConfig();
        if (mode_listener != null) mode_listener.onChange();
    }

    public boolean isTextMode() {
        return text_mode;
    }
    public void setTextMode(boolean is_text_mode) {
        text_mode = is_text_mode;
        saveConfig();
    }

    public boolean insertBlacklist(String x) {
        if (!blacklist.contains(x)) {
            blacklist.add(x);
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean removeBlacklist(String x) {
        if (blacklist.contains(x)) {
            blacklist.remove(x);
            saveConfig();
            return true;
        }
        return false;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    /**
     * 所有分类
     * @return
     */
    public List<Category> allCategories() {
        List<Category> list = new ArrayList<>();
        for(int x = 1; x < Constant.CATEGORY_COUNT; x ++) {
            list.add(new Category(Constant.CATEGORYS[x], x));
        }
        return list;
    }

    /**
     * 已选的分类
     * @param withFirst 是否包含第一个分类
     * @return
     */
    public List<Category> availableCategories(boolean withFirst) {
        List<Category> list = new ArrayList<>();
        if (withFirst) list.add(Category.getRecommentCategory());
        for(int x: available_categories) {
            list.add(new Category(Constant.CATEGORYS[x], x));
        }
        return list;
    }

    /**
     * 未选的分类
     * @return
     */
    public List<Category> unavailableCategories() {
        List<Category> list = new ArrayList<>();
        for(Integer x = 1; x < Constant.CATEGORY_COUNT; x ++)
            if (!available_categories.contains(x))
                list.add(new Category(Constant.CATEGORYS[x], x));
        return list;
    }

    /**
     * 添加分类
     * @param category 分类
     * @return 是否成功
     */
    public boolean addCategory(Category category) {
        if (!available_categories.contains(category.idx)) {
            available_categories.add(category.idx);
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 删除分类
     * @param category 分类
     * @return 是否成功
     */
    public boolean removeCategory(Category category) {
        if (available_categories.contains(category.idx)) {
            available_categories.remove((Integer)category.idx);
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * 切换分类的状态，已选变成未选，未选变成已选
     * @param idx
     */
    public void switchAvailable(Integer idx) {
        if (available_categories.contains(idx))
            available_categories.remove(idx);
        else available_categories.add(idx);
        saveConfig();
    }

    private void loadConfig() {
        JSONObject obj = new JSONObject();
        try {
            Scanner in = new Scanner(new FileInputStream(path));
            String content = "";
            while(in.hasNextLine()) content = content + in.nextLine();
            in.close();
            obj = new JSONObject(content);
        } catch(Exception e) {
            e.printStackTrace();
        }

        night_mode = obj.optBoolean("night_mode", false);
        text_mode = obj.optBoolean("text_mode", false);
        available_categories = new ArrayList<>();
        try {
            JSONArray array = obj.getJSONArray("available_categories");
            for(int i = 0; i < array.length(); i ++)
                available_categories.add(array.getInt(i));
        } catch(Exception e) {
            for(int i = 1; i < Constant.CATEGORY_COUNT; i ++)
                available_categories.add(i);
        }
        blacklist = new ArrayList<>();
        try {
            JSONArray array = obj.getJSONArray("blacklist");
            for(int i = 0; i < array.length(); i ++)
                blacklist.add(array.getString(i));
        } catch(Exception e) {

        }
    }

    void saveConfig() {

        Single.fromCallable(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("night_mode", night_mode);
                    obj.put("text_mode", text_mode);
                    JSONArray available_categories_array = new JSONArray();
                    for(int x: available_categories)
                        available_categories_array.put(x);
                    obj.put("available_categories", available_categories_array);
                    JSONArray blacklist_array = new JSONArray();
                    for(String x: blacklist)
                        blacklist_array.put(x);
                    obj.put("blacklist", blacklist_array);

                    OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path));
                    w.write(obj.toString());
                    w.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }).subscribeOn(save_thread).subscribe();
    }
}
