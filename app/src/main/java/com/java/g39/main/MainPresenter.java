package com.java.g39.main;

import com.java.g39.R;

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
}
