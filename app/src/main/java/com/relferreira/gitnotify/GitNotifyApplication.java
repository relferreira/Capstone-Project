package com.relferreira.gitnotify;

import android.app.Application;

/**
 * Created by relferreira on 10/30/16.
 */
public class GitNotifyApplication extends Application {


    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeInjector();
    }

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
