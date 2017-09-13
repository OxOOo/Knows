package com.java.g39.main;

import com.java.g39.R;
import com.java.g39.data.Config;
import com.java.g39.data.Manager;

/**
 * Created by equation on 9/7/17.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mMainView;
    private boolean mRestartByMode;
    private int mCurrentNavigation = R.id.nav_news;

    public MainPresenter(MainContract.View view, boolean restartByMode) {
        this.mMainView = view;
        this.mRestartByMode = restartByMode;
        view.setPresenter(this);

        if (mRestartByMode) {
            mCurrentNavigation = R.id.nav_settings;
        } else {
            mCurrentNavigation = R.id.nav_news;
        }
    }

    public int getCurrentNavigation() {
        return mCurrentNavigation;
    }

    @Override
    public void subscribe() {
        switchNavigation(mCurrentNavigation);
    }

    @Override
    public void unsubscribe() {
    }

    @Override
    public boolean isNightMode() {
        return Manager.I.getConfig().isNightMode();
    }

    @Override
    public void setConfigNightModeChangeListener(Config.NightModeChangeListener listener) {
        Manager.I.getConfig().setNightModeChangeListener(listener);
    }

    @Override
    public void switchNavigation(int id) {
        mCurrentNavigation = id;
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
}
