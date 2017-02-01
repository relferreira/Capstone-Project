package com.relferreira.gitnotify.ui.main;

import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.ui.base.BasePresenter;

/**
 * Created by relferreira on 10/29/16.
 */
public class MainPresenter extends BasePresenter<MainView> {

    AuthRepository authRepository;

    public MainPresenter(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public boolean checkIfIsLogged() {
        return authRepository.getAccount() != null;
    }
}
