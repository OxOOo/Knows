package com.java.g39;

import android.app.Application;

import com.java.g39.data.ImageLoader;
import com.java.g39.data.Manager;

/**
 * Created by equation on 9/11/17.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.init(this);

        // 创建数据管理
        Manager.CreateI(this);
    }
}
