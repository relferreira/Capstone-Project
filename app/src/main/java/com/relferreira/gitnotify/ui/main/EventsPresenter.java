package com.relferreira.gitnotify.ui.main;

import android.content.Context;

import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by relferreira on 2/5/17.
 */

public class EventsPresenter extends BasePresenter<EventsView> {

    private final EventsSyncAdapter eventsSyncAdapter;
    private Subscription subs;

    public EventsPresenter(EventsSyncAdapter eventsSyncAdapter){
        this.eventsSyncAdapter = eventsSyncAdapter;
    }

    public void requestSync(Context context) {
        subs = this.eventsSyncAdapter.syncImmediately(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (isViewAttached())
                        getView().showLoading(s != EventsSyncAdapter.STATUS_SUCCESS);
                }, error -> {
                    error.printStackTrace();
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
