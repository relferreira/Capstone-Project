package com.relferreira.gitnotify.util;

import com.relferreira.gitnotify.ui.base.BasePresenter;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by relferreira on 1/15/17.
 */
public class AuthErrorHelper {
    public static boolean onError(BasePresenter presenter, Throwable e){
        if(e instanceof HttpException && (((HttpException)e).code() == 404 || ((HttpException)e).code() == 401)){
            presenter.onUnauthorized();
            return true;
        }
        return false;
    }

}
