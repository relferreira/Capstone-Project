package com.relferreira.gitnotify.ui.login;

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

    public void loginRequest(String username, String password) {
        if (username == null || username.isEmpty()) {
            getView().showError("Username should not be empty");
            return;
        }

        if (password == null || password.isEmpty()) {
            getView().showError("Password should not be empty");
            return;
        }
        githubInteractor.login(username, password)
                .subscribe(events -> {
                    if(isViewAttached())
                        getView().goToMain();
                }, error -> {
                    getView().showError("Invalid login");
                });
    }

}
