package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.BuildConfig;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableLoginRequest;
import com.relferreira.gitnotify.model.Login;
import com.relferreira.gitnotify.model.LoginRequest;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.util.ApiInterceptor;
import com.relferreira.gitnotify.util.CriptographyProvider;
import com.relferreira.gitnotify.util.SchedulerProvider;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by relferreira on 2/5/17.
 */

public class GithubInteractor {

    private final CriptographyProvider criptographyProvider;
    private final GithubService githubService;
    private final AuthRepository authRepository;
    private final SchedulerProvider schedulerProvider;
    private final ApiInterceptor apiInterceptor;

    public GithubInteractor(CriptographyProvider criptographyProvider, GithubService githubService, AuthRepository authRepository,
                            SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor) {
        this.criptographyProvider = criptographyProvider;
        this.githubService = githubService;
        this.authRepository = authRepository;
        this.schedulerProvider = schedulerProvider;
        this.apiInterceptor = apiInterceptor;
    }

    public Observable<Login> login(String username, String password) {
        String credentials = username + ":" + password;
        final String basic = "Basic " + criptographyProvider.base64(credentials);
        LoginRequest loginRequest = ImmutableLoginRequest.builder()
                .addScopes("public_repo")
                .note("admin script")
                .clientId(BuildConfig.CLIENT_ID)
                .clientSecret(BuildConfig.CLIENT_SECRET)
                .build();
        apiInterceptor.setAuthValue(basic);
        return githubService.login(loginRequest)
                .compose(schedulerProvider.applySchedulers())
                .map(login -> {
                    authRepository.addAccount(username, basic);
                    return login;
                });
    }

    public Response<List<Organization>> listOrgsSync() throws IOException {
        return githubService.listOrgsSync().execute();
    }

    public Response<List<Event>> getEventsMeSync(String user) throws IOException {
        return githubService.getEventsMeSync(user).execute();
    }

    public Response<List<Event>> getEventsOrgsSync(String user, String organization) throws IOException {
        return githubService.getEventsOrgsSync(user, organization).execute();
    }

    public Observable<List<Comment>> getIssueComments(String owner, String repo, Integer issueId) {
        return githubService.listIssueComments(owner, repo, issueId);
    }
}
