package com.relferreira.gitnotify;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by relferreira on 1/15/17.
 */
public class ApiInterceptor implements Interceptor {

    public final static String AUTH_KEY = "auth_key";

    private String authValue = null;//"Basic cmVsZmVycmVpcmE6QHRvcnJpbmhvMTM="

    public void clearAuthValue() {
        authValue = null;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .method(original.method(), original.body())
                .header("Accept", "application/json");

        if(authValue != null)
            requestBuilder.header("Authorization", authValue);

        return chain.proceed(requestBuilder.build());
    }
}
