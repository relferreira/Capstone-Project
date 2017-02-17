package com.relferreira.gitnotify.ui.main;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.repository.data.OrganizationColumns;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.util.AnalyticsTracker;
import com.relferreira.gitnotify.util.Navigator;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_EVENT_ID = "arg_event_id";
    public static final String ARG_EVENT_TYPE = "arg_event_type";
    private static final int LOADER_ID = 1;
    private static final int COLUMNS_CONTAINER_ID = 76910294;

    private TabsAdapter adapter;

    @BindView(R.id.main_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tabs)  @Nullable
    TabLayout tabs;
    @BindView(R.id.main_viewpager)  @Nullable
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_loading)
    ProgressBar loadingProgressBar;
    @BindView(R.id.main_list) @Nullable
    HorizontalScrollView columnsList;
    @BindView(R.id.main_istablet) @Nullable
    View isTabletView;

    @Inject
    MainPresenter presenter;
    @Inject
    Navigator navigator;
    @Inject
    AnalyticsTracker tracker;

    private boolean loading;
    private boolean tabletMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tabletMode = isTabletView != null;
        if(!tabletMode) {
            adapter = new TabsAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            tabs.setupWithViewPager(viewPager);
        }

        presenter.attachView(this);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        tracker.sendScreenTrack("MainScreen");

        if(savedInstanceState == null) {
            // From widget
            if (getIntent().getStringExtra(ARG_EVENT_ID) != null)
                redirectToDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!presenter.checkIfIsLogged())
            navigator.goToLogin(this);
        syncRequest();
    }

    @Override
    protected void onDestroy() {
        presenter.dettachView();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem refreshItem = menu.findItem(R.id.sync);
        refreshItem.setVisible(loading);

        if(loading) {
            Drawable drawable = refreshItem.getIcon();
            Animatable anim = ((Animatable) drawable);
            if(!anim.isRunning())
                anim.start();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{OrganizationColumns.ID, OrganizationColumns.LOGIN};
        return new CursorLoader(this, GithubProvider.Organizations.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!tabletMode)
            constructTabs(data);
        else
            constructColumns(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void syncRequest() {
        presenter.requestSync(this);
    }

    @Override
    public void showLoading(boolean state) {
        if(this.loading != state) {
            this.loading = state;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void showError() {
        Snackbar.make(coordinatorLayout, getString(R.string.sync_error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.sync_retry), v -> syncRequest()).show();
    }

    private void constructTabs(Cursor data) {
        adapter.reset();
        String feedTabName = getString(R.string.main_tab_me);
        adapter.add(EventsFragment.newInstance(0, feedTabName, false, tabletMode), feedTabName);
        data.moveToFirst();
        while (!data.isAfterLast()) {
            int orgId = data.getInt(data.getColumnIndex(OrganizationColumns.ID));
            String tabName = data.getString(data.getColumnIndex(OrganizationColumns.LOGIN));
            adapter.add(EventsFragment.newInstance(orgId, tabName, true, tabletMode), tabName);
            data.moveToNext();
        }

        loadingProgressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void constructColumns(Cursor data) {
        columnsList.removeAllViews();

        LinearLayout listOfFragments = new LinearLayout(this);
        listOfFragments.setOrientation(LinearLayout.HORIZONTAL);
        listOfFragments.setId(COLUMNS_CONTAINER_ID);
        listOfFragments.setLayoutParams(new NestedScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String feedTabName = getString(R.string.main_tab_me);
        transaction.add(listOfFragments.getId(), EventsFragment.newInstance(0, feedTabName, false, tabletMode), feedTabName);
        data.moveToFirst();
        while (!data.isAfterLast()) {
            int orgId = data.getInt(data.getColumnIndex(OrganizationColumns.ID));
            String tabName = data.getString(data.getColumnIndex(OrganizationColumns.LOGIN));
            transaction.add(listOfFragments.getId(), EventsFragment.newInstance(orgId, tabName, true, tabletMode), tabName);
            data.moveToNext();
        }
        //Proposital change from `commit()`
        transaction.commitAllowingStateLoss();
        loadingProgressBar.setVisibility(View.GONE);
        columnsList.addView(listOfFragments);
    }

    private void redirectToDetails() {
        Intent intent = getIntent();
        String eventId = intent.getStringExtra(ARG_EVENT_ID);
        String type = intent.getStringExtra(ARG_EVENT_TYPE);
        navigator.gotToDetails(eventId, type, this, getSupportFragmentManager(), tabletMode);
    }
}
