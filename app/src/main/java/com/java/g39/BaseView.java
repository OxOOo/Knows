package com.java.g39;

import android.content.Context;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

    /**
     * @return 当前的上下文
     */
    Context getContext();
}
