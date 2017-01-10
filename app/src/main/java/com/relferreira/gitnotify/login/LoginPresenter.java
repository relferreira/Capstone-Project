package com.relferreira.gitnotify.login;

import com.relferreira.gitnotify.base.BasePresenter;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    public void loginRequest(String username, String password) {
        getView().showError("Invalid Username");
    }

}
