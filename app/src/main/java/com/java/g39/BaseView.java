package com.java.g39;

/**
 * Created by chenyu on 2017/9/7.
 */

public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
