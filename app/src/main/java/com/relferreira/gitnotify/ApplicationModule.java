package com.relferreira.gitnotify;

import android.app.Application;

import com.relferreira.gitnotify.login.LoginPresenter;
import com.relferreira.gitnotify.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by relferreira on 10/30/16.
 */
@Module
public class ApplicationModule {

    GitNotifyApplication app;
    public ApplicationModule(GitNotifyApplication application) {
        app = application;
    }
    @Provides
    @Singleton
    protected Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    protected MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Provides
    @Singleton
    protected LoginPresenter provideLoginPresenter() {
        return new LoginPresenter();
    }

}
