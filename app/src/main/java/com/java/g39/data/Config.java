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

    private NightModeChangeListener mode_listener = null;
    private boolean night_mode; // 夜间模式
    private boolean text_mode; // 无图模式/文字模式
    private List<Integer> available_categories;

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
     * @return
     */
    public List<Category> availableCategories() {
        List<Category> list = new ArrayList<>();
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
     * @param idx
     * @return 分类
     */
    public Category addCategory(Integer idx) {
        if (!available_categories.contains(idx)) {
            available_categories.add(idx);
            saveConfig();
            return new Category(Constant.CATEGORYS[idx], idx);
        }
        return null;
    }

    /**
     * 删除分类
     * @param idx
     * @return 是否成功
     */
    public boolean removeCategory(Integer idx) {
        if (available_categories.contains(idx)) {
            available_categories.remove(idx);
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
    }

    void saveConfig() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("night_mode", night_mode);
            obj.put("text_mode", text_mode);
            JSONArray array = new JSONArray();
            for(int x: available_categories)
                array.put(x);
            obj.put("available_categories", array);

            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path));
            w.write(obj.toString());
            w.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
