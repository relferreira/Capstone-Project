package com.relferreira.gitnotify;

import com.relferreira.gitnotify.login.LoginPresenter;
import com.relferreira.gitnotify.login.LoginView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    LoginView loginView;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter();
        presenter.attachView(loginView);
    }

    @Test
    public void shouldValidateEmptyUsername() {
        presenter.loginRequest(null, null);
        verify(loginView, atLeastOnce()).showError("Invalid Username");
    }

}