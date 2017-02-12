package com.relferreira.gitnotify.util;

import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.repository.interfaces.EtagRepository;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by relferreira on 1/15/17.
 */
public class ApiInterceptor implements Interceptor {

    public final static String AUTH_KEY = "auth_key";
    private final EtagRepository etagRepository;

    private String authValue = null;

    public ApiInterceptor(AuthRepository authRepository, EtagRepository etagRepository) {
        this.authValue = authRepository.getToken();
        this.etagRepository = etagRepository;
    }

    public void clearAuthValue() {
        authValue = null;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString();

        Boolean useCache = original.header("Use-Cache") == null || Boolean.parseBoolean(original.header("Use-Cache"));

        Request.Builder requestBuilder = original.newBuilder()
                .method(original.method(), original.body())
                .header("Accept", "application/json");
        if(authValue != null)
            requestBuilder.header("Authorization", authValue);

        String etag = etagRepository.getEtag(url);
        if(useCache && etag != null)
            requestBuilder.header("If-None-Match", etag);

        Response response = chain.proceed(requestBuilder.build());
        etag = response.header("ETag");
        if(useCache && etag != null){
            etagRepository.setEtag(url, etag);
        }

        return response;
    }
}
