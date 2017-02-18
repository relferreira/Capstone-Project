package com.relferreira.gitnotify.ui.main;

import android.content.Context;

import com.relferreira.gitnotify.domain.AuthInteractor;
import com.relferreira.gitnotify.domain.CacheInteractor;
import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.OrganizationInteractor;
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
    private final OrganizationInteractor organizationInteractor;
    private final EventInteractor eventInteractor;
    private CacheInteractor cacheInteractor;
    private final AuthInteractor authInteractor;
    private Subscription subs;

    public MainPresenter(EventsSyncAdapter eventsSyncAdapter, AuthInteractor authInteractor,
                         OrganizationInteractor organizationInteractor, EventInteractor eventInteractor, CacheInteractor cacheInteractor){
        this.authInteractor = authInteractor;
        this.eventsSyncAdapter = eventsSyncAdapter;
        this.organizationInteractor = organizationInteractor;
        this.eventInteractor = eventInteractor;
        this.cacheInteractor = cacheInteractor;
    }

    public boolean checkIfIsLogged() {
        return authInteractor.getAccount() != null;
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
                    if(isViewAttached()) {
                        getView().showLoading(false);
                        getView().showError();
                    }
                });
    }

    public void logout() {
        authInteractor.removeAccount();
        organizationInteractor.removeOrganizations();
        eventInteractor.removeEvents();
        cacheInteractor.invalidateCache();
        if(isViewAttached())
            getView().redirectToLogin();
    }

    @Override
    public void dettachView() {
        super.dettachView();
        if(subs != null && !subs.isUnsubscribed())
            subs.unsubscribe();
    }
}
