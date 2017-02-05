package com.relferreira.gitnotify.injector;

import android.content.Context;
import android.content.SharedPreferences;

import com.relferreira.gitnotify.repository.AuthManagerRepository;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.repository.interfaces.EtagRepository;
import com.relferreira.gitnotify.repository.EtagSharedPreferencesRepository;
import com.relferreira.gitnotify.repository.EventDbRepository;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;
import com.relferreira.gitnotify.repository.LogAndroidRepository;
import com.relferreira.gitnotify.repository.interfaces.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationDbRepository;
import com.relferreira.gitnotify.repository.interfaces.OrganizationRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by relferreira on 1/27/17.
 */
@Module
public class RepositoryModule {

    @Provides
    protected LogRepository providesLogRepository() {
        return new LogAndroidRepository();
    }

    @Provides
    @Singleton
    protected EtagRepository providesEtagRepository(SharedPreferences sharedPreferences) {
        return new EtagSharedPreferencesRepository(sharedPreferences);
    }

    @Provides
    @Singleton
    protected AuthRepository provideAuthRepository(Context context){
        return new AuthManagerRepository(context);
    }

    @Provides
    @Singleton
    protected EventRepository provideEventRepository(Context context, LogRepository logRepository) {
        return new EventDbRepository(context, logRepository);
    }

    @Provides
    @Singleton
    protected OrganizationRepository provideOrganizationRepository(Context context) {
        return new OrganizationDbRepository(context);
    }
}
