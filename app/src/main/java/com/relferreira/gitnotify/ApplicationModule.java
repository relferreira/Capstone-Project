package com.relferreira.gitnotify;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.auth.Authenticator;
import com.relferreira.gitnotify.login.LoginPresenter;
import com.relferreira.gitnotify.main.MainPresenter;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.EventRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
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
    protected Authenticator provideAuthenticator(Context context) {
        return new Authenticator(context);
    }

    @Provides
    @Singleton
    protected EventsSyncAdapter provideEventsSyncAdapter(Context context, AuthRepository authRepository, OrganizationRepository organizationRepository,
                                                         EventRepository eventRepository, GithubService githubService, LogRepository logRepository) {
        return new EventsSyncAdapter(context, authRepository, organizationRepository, eventRepository, githubService, logRepository, true);
    }

    @Provides
    @Singleton
    protected LoginPresenter provideLoginPresenter(SchedulerProvider schedulerProvider, ApiInterceptor apiInterceptor, GithubService githubService, CriptographyProvider criptographyProvider, AuthRepository authRepository) {
        return new LoginPresenter(schedulerProvider, apiInterceptor, githubService, criptographyProvider, authRepository);
    }

}
