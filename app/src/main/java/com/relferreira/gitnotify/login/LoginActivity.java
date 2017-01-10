package com.relferreira.gitnotify.login;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.relferreira.gitnotify.ApplicationComponent;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.login_username) EditText editTextUsername;
    @BindView(R.id.login_password) EditText editTextPassword;
    @BindView(R.id.login_btn)
    Button btnLogin;
    @Inject LoginPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void showError(String msg) {

    }
}
