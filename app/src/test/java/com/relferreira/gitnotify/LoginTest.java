package com.relferreira.gitnotify;

import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.GsonAdaptersModel;
import com.relferreira.gitnotify.model.ImmutableLogin;
import com.relferreira.gitnotify.model.Login;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.ui.login.LoginPresenter;
import com.relferreira.gitnotify.ui.login.LoginView;
import com.relferreira.gitnotify.util.ApiInterceptor;
import com.relferreira.gitnotify.util.CriptographyProvider;
import com.relferreira.gitnotify.util.SchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {

    @Mock
    LoginView loginView;
    @Mock
    GithubService githubService;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    ApiInterceptor apiInterceptor;
    @Mock
    CriptographyProvider criptographyProvider;
    @Mock
    AuthRepository authRepository;

    private LoginPresenter presenter;
    private GithubInteractor interactor;

    @Before
    public void setUp() {
        SchedulerProvider schedulerProvider = new SchedulerProvider() {
            @Override
            public <T> Observable.Transformer<T, T> applySchedulers() {
                return observable -> observable.subscribeOn(Schedulers.immediate())
                        .observeOn(Schedulers.immediate());
            }
        };
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new GsonAdaptersModel())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        interactor = new GithubInteractor(criptographyProvider, githubService, authRepository, schedulerProvider, apiInterceptor, gson);
        presenter = new LoginPresenter(interactor);
        presenter.attachView(loginView);
    }

    @Test
    public void shouldValidateInvalidLogin() {
        when(githubService.login(any())).thenReturn(Observable.error(new Exception("Invalid login")));
        when(criptographyProvider.base64(any())).thenReturn("123");
        interactor.login("relferreira", "teste").subscribe(login -> {}, error -> {});
        verify(authRepository, never()).addAccount("relferreira", "Basic 123");
    }

    @Test
    public void shouldStoreUserAccount() {
        Login loginResult = ImmutableLogin.builder()
                .id("1234")
                .token("1234")
                .url("https://github.com")
                .build();
        when(githubService.login(any())).thenReturn(Observable.just(loginResult));
        when(criptographyProvider.base64(any())).thenReturn("123");
        interactor.login("relferreira", "teste").subscribe();
        verify(authRepository).addAccount("relferreira", "Basic 123");
    }

    @Test
    public void shouldValidateEmptyUsername() {
        presenter.loginRequest(null, null);
        verify(loginView).showError("Username should not be empty");
    }

    @Test
    public void shouldValidateEmptyPassword() {
        presenter.loginRequest("relferreira", null);
        verify(loginView).showError("Password should not be empty");
    }

    @Test
    public void shouldValidateInvalidLoginPresenter() {
        String login = "relferreira",
                password = "teste";
        when(githubService.login(any())).thenReturn(Observable.error(new Exception("Invalid login")));
        when(criptographyProvider.base64(any())).thenReturn("123");
        presenter.loginRequest(login, password);
        verify(loginView).showError("Invalid login");
    }

    @Test
    public void shouldStoreAuthInformations() {
        Login loginResult = ImmutableLogin.builder()
                .id("1234")
                .token("1234")
                .url("https://github.com")
                .build();
        when(githubService.login(any())).thenReturn(Observable.just(loginResult));
        when(criptographyProvider.base64(any())).thenReturn("123");
        presenter.loginRequest("relferreira", "teste");
        verify(loginView).goToMain();
    }

}