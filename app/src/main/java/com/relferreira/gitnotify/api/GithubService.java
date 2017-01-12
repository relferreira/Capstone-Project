package com.relferreira.gitnotify.api;

import com.relferreira.gitnotify.model.User;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by relferreira on 1/10/17.
 */
public interface GithubService {

    @GET("users/{user}")
    Observable<User> getUser(@Path("user") String user);
}