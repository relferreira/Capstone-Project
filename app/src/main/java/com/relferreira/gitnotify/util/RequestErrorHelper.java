package com.relferreira.gitnotify.util;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by relferreira on 1/28/17.
 */
public class RequestErrorHelper {

    public static int getCode(Throwable e) {
        if(e instanceof HttpException)
            return ((HttpException)e).code();
        else if(e instanceof MockHttpException)
            return ((MockHttpException)e).code();
        return 0;
    }
}
