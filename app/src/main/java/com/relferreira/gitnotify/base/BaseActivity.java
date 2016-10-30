package com.relferreira.gitnotify.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by relferreira on 10/29/16.
 */
public abstract class BaseActivity<V extends BasePresenter> extends AppCompatActivity implements BaseView{

    protected V presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null)
            presenter.dettachView();
    }

    @Override
    public void showLoading(boolean state) {

    }

    public abstract V createPresenter();
}
