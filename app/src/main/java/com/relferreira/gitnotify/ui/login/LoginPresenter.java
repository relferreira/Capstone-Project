package com.relferreira.gitnotify.ui.login;

import android.content.Context;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private GithubInteractor githubInteractor;

    @Inject
    public LoginPresenter(GithubInteractor githubInteractor) {
        this.githubInteractor = githubInteractor;
    }

    public void loginRequest(Context context, String username, String password) {
        if (username == null || username.isEmpty()) {
            getView().showError(context.getString(R.string.login_username_empty));
            return;
        }

        if (password == null || password.isEmpty()) {
            getView().showError(context.getString(R.string.login_password_empty));
            return;
        }
        githubInteractor.login(username, password)
                .subscribe(events -> {
                    if(isViewAttached())
                        getView().goToMain();
                }, error -> {
                    getView().showError(context.getString(R.string.login_invalid));
                });
    }

}
