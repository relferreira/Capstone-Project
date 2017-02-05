package com.relferreira.gitnotify.ui.main;

import android.content.Context;
import android.util.Log;

import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by relferreira on 10/29/16.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private final EventsSyncAdapter eventsSyncAdapter;
    private final AuthRepository authRepository;
    private Subscription subs;

    public MainPresenter(EventsSyncAdapter eventsSyncAdapter, AuthRepository authRepository){
        this.authRepository = authRepository;
        this.eventsSyncAdapter = eventsSyncAdapter;
    }

    public boolean checkIfIsLogged() {
        return authRepository.getAccount() != null;
    }

    public void requestSync(Context context) {
        if(!checkIfIsLogged())
            return;
        subs = this.eventsSyncAdapter.syncImmediately(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (isViewAttached())
                        getView().showLoading(s == EventsSyncAdapter.STATUS_PROGRESS);
                }, error -> {
                    error.printStackTrace();
                    Log.e("teste", error.toString());
                    if(isViewAttached()) {
                        getView().showLoading(false);
                        getView().showError();
                    }
                });
    }

    @Override
    public void dettachView() {
        super.dettachView();
        if(subs != null && !subs.isUnsubscribed())
            subs.unsubscribe();
    }
}
