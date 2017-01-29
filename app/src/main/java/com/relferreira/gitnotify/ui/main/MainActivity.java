package com.relferreira.gitnotify.ui.main;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.relferreira.gitnotify.ApplicationComponent;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.util.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainView {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    MainPresenter presenter;
    @Inject
    Navigator navigator;

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        resources = getResources();

        setSupportActionBar(toolbar);

        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.add(GitNotificationsFragment.newInstance(), "teste1");
        adapter.add(GitNotificationsFragment.newInstance(), "teste2");
        adapter.add(GitNotificationsFragment.newInstance(), "teste3");

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        presenter.attachView(this);
        presenter.loadToastMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!presenter.checkIfIsLogged())
            navigator.goToLogin(this);
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
