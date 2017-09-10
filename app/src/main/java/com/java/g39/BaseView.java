package com.java.g39;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

    /**
     * @return 当前的上下文
     */
    Context context();

    /**
     * @param intent 调用startActivity(intent)
     */
    void start(Intent intent, Bundle options);
}
