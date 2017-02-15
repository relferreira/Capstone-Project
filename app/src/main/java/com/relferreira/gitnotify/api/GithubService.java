package com.relferreira.gitnotify.api;

import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Login;
import com.relferreira.gitnotify.model.LoginRequest;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by relferreira on 1/10/17.
 */
public interface GithubService {

    @GET("users/{user}")
    Observable<User> getUser(@Path("user") String user);

    @GET("users/{user}/received_events")
    Observable<List<Event>> getEventsMe(@Path("user") String user);

    @GET("users/{user}/received_events")
    Call<List<Event>> getEventsMeSync(@Path("user") String user);

    @GET("users/{user}/events/orgs/{organization}")
    Observable<List<Event>> getEventsOrgs(@Path("user") String user, @Path("organization") String organization);

    @GET("users/{user}/events/orgs/{organization}")
    Call<List<Event>> getEventsOrgsSync(@Path("user") String user, @Path("organization") String organization);

    @POST("authorizations")
    Observable<Login> login(@Body LoginRequest loginRequest);

    @GET("user/orgs")
    Observable<List<Organization>> listOrgs();

    @GET("user/orgs")
    Call<List<Organization>> listOrgsSync();

    @Headers("Use-Cache: false")
    @GET("repos/{owner}/{repo}/issues/{issueId}/comments")
    Observable<List<Comment>> listIssueComments(@Path("owner") String owner, @Path("repo") String repo,
                                                @Path("issueId") Integer issueId, @Query("page") Integer page);

    @Headers("Use-Cache: false")
    @GET("repos/{owner}/{repo}/pulls/{id}/comments")
    Observable<List<Comment>> listPullComments(@Path("owner") String owner, @Path("repo") String repo,
                                               @Path("id") Integer id, @Query("page") Integer page);

    @Headers("Use-Cache: false")
    @GET("repos/{owner}/{repo}/commits/{id}/comments")
    Observable<List<Comment>> listCommitComments(@Path("owner") String owner, @Path("repo") String repo,
                                               @Path("id") String id, @Query("page") Integer page);
}