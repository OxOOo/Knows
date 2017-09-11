package com.java.g39.main;

import android.widget.Toast;

import com.java.g39.R;
import com.java.g39.data.Manager;

import io.reactivex.functions.Consumer;

/**
 * Created by equation on 9/7/17.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;

    public MainPresenter(MainContract.View view) {
        this.mMainView = view;
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mMainView.switchToNews();
    }

    @Override
    public void unsubscribe() {
    }

    @Override
    public void switchNavigation(int id) {
        switch (id) {
            case R.id.nav_news:
                mMainView.switchToNews();
                break;
            case R.id.nav_favorites:
                mMainView.switchToFavorites();
                break;
            case R.id.nav_settings:
                mMainView.switchToSettings();
                break;
            case R.id.nav_about:
                mMainView.switchToAbout();
                break;
            default:
                break;
        }
    }

    @Override
    public void clean() {
        Manager.I.clean().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) Toast.makeText(mMainView.context(), "清空缓存成功", Toast.LENGTH_LONG).show();
                else Toast.makeText(mMainView.context(), "清空缓存失败", Toast.LENGTH_LONG).show();
            }
        });
    }
}
