package com.relferreira.gitnotify.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.relferreira.gitnotify.ApplicationComponent;
import com.relferreira.gitnotify.GitNotifyApplication;
import com.relferreira.gitnotify.ui.login.LoginActivity;

/**
 * Created by relferreira on 10/29/16.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectActivity(getApplicationComponent());
    }


    @Override
    public void showLoading(boolean state) {

    }

    @Override
    public void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((GitNotifyApplication) getApplication()).getApplicationComponent();
    }

    public abstract void injectActivity(ApplicationComponent component);

}
