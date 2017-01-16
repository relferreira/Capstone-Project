package com.relferreira.gitnotify;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.login.LoginPresenter;
import com.relferreira.gitnotify.main.MainPresenter;
import com.relferreira.gitnotify.util.CriptographyProvider;

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
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    protected MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Provides
    @Singleton
    SchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    @Singleton
    CriptographyProvider provideCriptographyProvider() {
        return new CriptographyProvider();
    }
    @Provides
    @Singleton
    protected LoginPresenter provideLoginPresenter(SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor, GithubService githubService, CriptographyProvider criptographyProvider, SharedPreferences sharedPreferences) {
        return new LoginPresenter(schedulerProvider, apiInterceptor, githubService, criptographyProvider, sharedPreferences);
    }

}
