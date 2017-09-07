package com.java.g39;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface BasePresenter {

    /**
     * 启动事务
     */
    void subscribe();

    /**
     * 取消事务
     */
    void unsubscribe();
}
