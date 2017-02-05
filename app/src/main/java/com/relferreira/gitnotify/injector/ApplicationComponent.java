package com.relferreira.gitnotify.injector;

import com.relferreira.gitnotify.auth.AuthenticatorService;
import com.relferreira.gitnotify.ui.login.LoginActivity;
import com.relferreira.gitnotify.ui.main.EventsFragment;
import com.relferreira.gitnotify.ui.main.MainActivity;
import com.relferreira.gitnotify.sync.EventsSyncService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by relferreira on 10/30/16.
 */

@Singleton
@Component(modules = {RepositoryModule.class, DomainModule.class, NetworkModule.class, ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(AuthenticatorService service);
    void inject(EventsSyncService service);
    void inject(EventsFragment fragment);
}
