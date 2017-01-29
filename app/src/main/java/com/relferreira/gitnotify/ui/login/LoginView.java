package com.relferreira.gitnotify.ui.login;

import com.relferreira.gitnotify.ui.base.BaseView;

/**
 * Created by relferreira on 1/9/17.
 */
public interface LoginView extends BaseView {
    void showError(String msg);
    void goToMain();
}
