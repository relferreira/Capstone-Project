package com.relferreira.gitnotify;

import com.relferreira.gitnotify.ui.base.BasePresenter;

import rx.Subscriber;

/**
 * Created by relferreira on 1/15/17.
 */
public abstract class AuthSubscriber<T> extends Subscriber<T> {

    private BasePresenter presenter;

    public AuthSubscriber(BasePresenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void onCompleted() {
        onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        System.out.print("teste");
    }

    @Override
    public void onNext(T t) {

    }
}
