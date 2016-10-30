package com.relferreira.gitnotify.main;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        resources = getResources();

        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.add(GitNotificationsFragment.newInstance(), "teste1");
        adapter.add(GitNotificationsFragment.newInstance(), "teste2");
        adapter.add(GitNotificationsFragment.newInstance(), "teste3");

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        presenter.loadToastMsg();
    }

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
