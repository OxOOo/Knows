package com.java.g39.data;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Created by chenyu on 2017/9/12.
 * 配置相关
 */

public class Config {
    private String path;

    private boolean night_mode; // 夜间模式
    private boolean text_mode; // 无图模式/文字模式

    Config(Context context) {
        path = context.getFilesDir().getPath() + "/config.json";
        loadConfig();
    }

    public boolean isNightMode() {
        return night_mode;
    }
    public void setNightMode(boolean is_night_mode) {
        night_mode = is_night_mode;
        saveConfig();
    }

    public boolean isTextMode() {
        return text_mode;
    }
    public void setTextMode(boolean is_text_mode) {
        text_mode = is_text_mode;
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
    }

    private void saveConfig() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("night_mode", night_mode);
            obj.put("text_mode", text_mode);

            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path));
            w.write(obj.toString());
            w.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
