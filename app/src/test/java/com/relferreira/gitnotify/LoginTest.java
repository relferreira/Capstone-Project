package com.relferreira.gitnotify;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.login.LoginPresenter;
import com.relferreira.gitnotify.login.LoginView;
import com.relferreira.gitnotify.model.ImmutableUser;
import com.relferreira.gitnotify.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {

    @Mock
    LoginView loginView;
    @Mock
    GithubService githubService;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        SchedulerProvider schedulerProvider = new SchedulerProvider() {
            @Override
            public <T> Observable.Transformer<T, T> applySchedulers() {
                return observable -> observable.subscribeOn(Schedulers.immediate())
                        .observeOn(Schedulers.immediate());
            }
        };
        presenter = new LoginPresenter(schedulerProvider, githubService);
        presenter.attachView(loginView);
    }

    @Test
    public void shouldValidateEmptyUsername() {
        presenter.loginRequest(null, null);
        verify(loginView, atLeastOnce()).showError("Username should not be empty");
    }

    @Test
    public void shouldValidateEmptyPassword() {
        presenter.loginRequest("relferreira", null);
        verify(loginView, atLeastOnce()).showError("Password should not be empty");
    }

    @Test
    public void shouldValidateInvalidLogin() {
        when(githubService.getUser("relferreira")).thenReturn(Observable.error(new Exception("Invalid login")));
        presenter.loginRequest("relferreira", "teste");
        verify(loginView, atLeastOnce()).showError("Invalid login");
    }

}