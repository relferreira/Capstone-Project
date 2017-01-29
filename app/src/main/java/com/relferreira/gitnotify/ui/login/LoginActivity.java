package com.relferreira.gitnotify.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.relferreira.gitnotify.ApplicationComponent;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.login_username) EditText editTextUsername;
    @BindView(R.id.login_password) EditText editTextPassword;
    @BindView(R.id.login_btn) Button btnLogin;
    @Inject LoginPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettachView();
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.login_btn)
    public void submit() {
        presenter.loginRequest(editTextUsername.getText().toString(), editTextPassword.getText().toString());
    }
}
