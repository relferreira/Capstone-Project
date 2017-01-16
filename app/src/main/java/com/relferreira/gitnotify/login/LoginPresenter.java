package com.relferreira.gitnotify.login;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.relferreira.gitnotify.ApiInterceptor;
import com.relferreira.gitnotify.BuildConfig;
import com.relferreira.gitnotify.SchedulerProvider;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.base.BasePresenter;
import com.relferreira.gitnotify.model.ImmutableLoginRequest;
import com.relferreira.gitnotify.model.LoginRequest;
import com.relferreira.gitnotify.util.AuthErrorHelper;
import com.relferreira.gitnotify.util.CriptographyProvider;

import javax.inject.Inject;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private final GithubService githubService;
    private final SchedulerProvider schedulerProvider;
    private final ApiInterceptor apiInterceptor;
    private final CriptographyProvider criptographyProvider;
    private final SharedPreferences sharedPreferences;

    @Inject
    public LoginPresenter(SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor,
                          GithubService githubService, CriptographyProvider criptographyProvider, SharedPreferences sharedPreferences) {
        this.githubService = githubService;
        this.schedulerProvider = schedulerProvider;
        this.apiInterceptor = apiInterceptor;
        this.criptographyProvider = criptographyProvider;
        this.sharedPreferences = sharedPreferences;
    }

    public void loginRequest(String username, String password) {
        if (username == null || username.isEmpty()) {
            getView().showError("Username should not be empty");
            return;
        }

        if (password == null || password.isEmpty()) {
            getView().showError("Password should not be empty");
            return;
        }
        String credentials = username + ":" + password;
        final String basic = "Basic " + criptographyProvider.base64(credentials);
        apiInterceptor.setAuthValue(basic);
        LoginRequest loginRequest = ImmutableLoginRequest.builder()
                .addScopes("public_repo")
                .note("admin script")
                .clientId(BuildConfig.CLIENT_ID)
                .clientSecret(BuildConfig.CLIENT_SECRET)
                .build();
        this.githubService.login(loginRequest)
                .compose(schedulerProvider.applySchedulers())
                .subscribe(events -> {
                    this.sharedPreferences.edit()
                            .putString(ApiInterceptor.AUTH_KEY, basic)
                            .commit();
                }, error -> {
                    if (!AuthErrorHelper.onError(this, error))
                        getView().showError("Invalid login");
                });

    }

}
