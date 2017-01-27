package com.relferreira.gitnotify;

import com.relferreira.gitnotify.auth.AuthenticatorService;
import com.relferreira.gitnotify.base.BaseActivity;
import com.relferreira.gitnotify.login.LoginActivity;
import com.relferreira.gitnotify.main.MainActivity;
import com.relferreira.gitnotify.sync.EventsSyncService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by relferreira on 10/30/16.
 */

@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(AuthenticatorService service);
    void inject(EventsSyncService service);
}
