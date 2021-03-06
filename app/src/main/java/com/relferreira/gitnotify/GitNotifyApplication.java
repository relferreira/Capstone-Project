package com.relferreira.gitnotify;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.injector.ApplicationModule;
import com.relferreira.gitnotify.injector.DaggerApplicationComponent;
import com.relferreira.gitnotify.injector.NetworkModule;

/**
 * Created by relferreira on 10/30/16.
 */
public class GitNotifyApplication extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        initializeInjector();
    }

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
