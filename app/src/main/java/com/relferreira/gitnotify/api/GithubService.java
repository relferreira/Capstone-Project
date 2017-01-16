package com.relferreira.gitnotify.api;

import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Login;
import com.relferreira.gitnotify.model.LoginRequest;
import com.relferreira.gitnotify.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by relferreira on 1/10/17.
 */
public interface GithubService {

    @GET("users/{user}")
    Observable<User> getUser(@Path("user") String user);

    @GET("users/{user}/events/orgs/{organization}")
    Observable<List<Event>> getOrgs(@Path("user") String user, @Path("organization") String organization);

    @POST("authorizations")
    Observable<Login> login(@Body LoginRequest loginRequest);
}