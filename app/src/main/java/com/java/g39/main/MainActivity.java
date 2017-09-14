package com.java.g39.main;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.java.g39.R;
import com.java.g39.about.AboutFragment;
import com.java.g39.news.NewsFragment;
import com.java.g39.favorites.FavoritesFragment;
import com.java.g39.settings.SettingsFragment;

/**
 * Created by equation on 9/7/17.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private static final String RESTART_BY_MODE = "RESTART_BY_MODE";

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private MainContract.Presenter mPresenter;
    private Fragment mNews, mFavorites, mSettings, mAbout;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this, getIntent().getBooleanExtra(RESTART_BY_MODE, false));

        if (mPresenter.isNightMode()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        mPresenter.setConfigNightModeChangeListener(() -> { // 白天/夜晚主题切换
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra(RESTART_BY_MODE, true);
                MainActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
                MainActivity.this.finish();
            }, 300);
        });

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchItem.setVisible(R.id.nav_news == mPresenter.getCurrentNavigation());
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setOnCloseListener(() -> {
            if (!mKeyword.isEmpty()) {
                mKeyword = "";
                ((NewsFragment) mNews).setKeyword("");
            }
            return false;
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mKeyword = query;
                System.out.println(mPresenter.getCurrentNavigation() + "  " + R.id.nav_news);
                if (mPresenter.getCurrentNavigation() == R.id.nav_news && mNews != null)
                    ((NewsFragment) mNews).setKeyword(query);
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mPresenter.switchNavigation(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    public void switchTo(int id, String title) {
        mToolbar.setTitle(title);
        if (mSearchItem != null) {
            mSearchItem.setVisible(R.id.nav_news == id);
        }
        if (mSearchView != null) {
            mSearchView.clearFocus();
            mSearchView.setQuery(mKeyword, false);
        }
        mNavigationView.setCheckedItem(id);
    }

    @Override
    public void switchToNews() {
        switchTo(R.id.nav_news, "新闻");
        if (mNews == null)
            mNews = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mNews).commit();
    }

    @Override
    public void switchToFavorites() {
        switchTo(R.id.nav_favorites, "收藏");
        if (mFavorites == null)
            mFavorites = FavoritesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mFavorites).commit();
    }

    @Override
    public void switchToSettings() {
        switchTo(R.id.nav_settings, "设置");
        if (mSettings == null)
            mSettings = SettingsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mSettings).commit();
    }

    @Override
    public void switchToAbout() {
        switchTo(R.id.nav_about, "关于");
        if (mAbout == null)
            mAbout = new AboutFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mAbout).commit();
    }
}
