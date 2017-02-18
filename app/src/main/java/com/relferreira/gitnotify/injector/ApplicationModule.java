package com.relferreira.gitnotify.injector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.relferreira.gitnotify.GitNotifyApplication;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.auth.Authenticator;
import com.relferreira.gitnotify.domain.AuthInteractor;
import com.relferreira.gitnotify.domain.CacheInteractor;
import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.domain.OrganizationInteractor;
import com.relferreira.gitnotify.repository.interfaces.LogRepository;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.ui.detail.DetailPresenter;
import com.relferreira.gitnotify.ui.login.LoginPresenter;
import com.relferreira.gitnotify.ui.main.EventsPresenter;
import com.relferreira.gitnotify.ui.main.MainPresenter;
import com.relferreira.gitnotify.util.AnalyticsTracker;
import com.relferreira.gitnotify.util.CriptographyProvider;
import com.relferreira.gitnotify.util.Navigator;
import com.relferreira.gitnotify.util.SchedulerProvider;

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
    protected Tracker providesTracker(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        return analytics.newTracker(R.xml.global_tracker);
    }

    @Provides
    @Singleton
    protected AnalyticsTracker providesAnalyticsTracker(Tracker tracker) {
        return new AnalyticsTracker(tracker);
    }

    @Provides
    @Singleton
    protected Navigator provideNavigator() {
        return new Navigator();
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
    protected EventsSyncAdapter provideEventsSyncAdapter(Context context, AuthInteractor authInteractor, OrganizationInteractor organizationInteractor,
                                                         EventInteractor eventInteractor, GithubInteractor githubInteractor, LogRepository logRepository) {
        return new EventsSyncAdapter(context, authInteractor, organizationInteractor, eventInteractor, githubInteractor, logRepository, true);
    }

    @Provides
    @Singleton
    protected LoginPresenter provideLoginPresenter(GithubInteractor githubInteractor) {
        return new LoginPresenter(githubInteractor);
    }

    @Provides
    @Singleton
    protected MainPresenter provideMainPresenter(EventsSyncAdapter eventsSyncAdapter, AuthInteractor authInteractor,
                                                 OrganizationInteractor organizationInteractor, EventInteractor eventInteractor, CacheInteractor cacheInteractor) {
        return new MainPresenter(eventsSyncAdapter, authInteractor, organizationInteractor, eventInteractor, cacheInteractor);
    }

    @Provides
    protected EventsPresenter provideEventPresenter(EventsSyncAdapter eventsSyncAdapter) {
        return new EventsPresenter(eventsSyncAdapter);
    }

    @Provides
    protected DetailPresenter provideDetailPresenter(StringRepository stringRepository, EventInteractor eventInteractor,
                                                     GithubInteractor githubInteractor, SchedulerProvider schedulerProvider) {
        return new DetailPresenter(stringRepository, eventInteractor, githubInteractor, schedulerProvider);
    }
}
