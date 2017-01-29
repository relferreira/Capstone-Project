package com.relferreira.gitnotify.ui.base;

/**
 * Created by relferreira on 10/29/16.
 */
public interface Presenter<V extends BaseView> {

    void attachView(V view);

    void dettachView();

    boolean isViewAttached();

    V getView();

    void onUnauthorized();

}
