package com.java.g39.main;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.view.View;
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

    private Toolbar mToolbar;
    private MainContract.Presenter mPresenter;
    private Fragment mNews, mFavorites, mSettings, mAbout;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPresenter = new MainPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
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

        mToolbar.setTitle(item.getTitle());
        mSearchItem.setVisible(item.getItemId() == R.id.nav_news);
        mSearchView.clearFocus();
        mSearchView.setQuery(mKeyword, false);
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

    @Override
    public void switchToNews() {
        if (mNews == null)
            mNews = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mNews).commit();
    }

    @Override
    public void switchToFavorites() {
        if (mFavorites == null)
            mFavorites = FavoritesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mFavorites).commit();
    }

    @Override
    public void switchToSettings() {
        if (mSettings == null)
            mSettings = SettingsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mSettings).commit();
    }

    @Override
    public void switchToAbout() {
        if (mAbout == null)
            mAbout = new AboutFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mAbout).commit();
    }
}
