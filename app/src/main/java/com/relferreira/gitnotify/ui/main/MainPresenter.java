package com.relferreira.gitnotify.ui.main;

import android.content.Context;

import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.ui.base.BasePresenter;

/**
 * Created by relferreira on 10/29/16.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private final EventsSyncAdapter eventsSyncAdapter;
    private final AuthRepository authRepository;

    public MainPresenter(EventsSyncAdapter eventsSyncAdapter, AuthRepository authRepository){
        this.authRepository = authRepository;
        this.eventsSyncAdapter = eventsSyncAdapter;
    }

    public boolean checkIfIsLogged() {
        return authRepository.getAccount() != null;
    }

    public void requestSync(Context context) {
//        if(checkIfIsLogged())
//            this.eventsSyncAdapter.syncImmediately(context);
    }
}
