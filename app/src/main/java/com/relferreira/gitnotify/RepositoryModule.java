package com.relferreira.gitnotify;

import android.content.Context;
import android.content.SharedPreferences;

import com.relferreira.gitnotify.repository.AuthManagerRepository;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.EtagRepository;
import com.relferreira.gitnotify.repository.EtagSharedPreferencesRepository;
import com.relferreira.gitnotify.repository.LogAndroidRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationDbRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;

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
    protected OrganizationRepository provideOrganizationRepository(Context context) {
        return new OrganizationDbRepository(context);
    }
}
