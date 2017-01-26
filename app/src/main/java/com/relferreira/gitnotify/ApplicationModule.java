package com.relferreira.gitnotify;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.repository.AuthManagerRepository;
import com.relferreira.gitnotify.repository.AuthRepository;
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
    protected Context providesApplicationContext() {
        return app;
    }

    @Provides
    @Singleton
    protected SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    protected MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Provides
    @Singleton
    protected SchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    @Singleton
    protected CriptographyProvider provideCriptographyProvider() {
        return new CriptographyProvider();
    }

    @Provides
    @Singleton
    protected AuthRepository provideAuthRepository(Context context){
        return new AuthManagerRepository(context);
    }

    @Provides
    @Singleton
    protected LoginPresenter provideLoginPresenter(SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor, GithubService githubService, CriptographyProvider criptographyProvider, AuthRepository authRepository) {
        return new LoginPresenter(schedulerProvider, apiInterceptor, githubService, criptographyProvider, authRepository);
    }

}
