package com.relferreira.gitnotify.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.relferreira.gitnotify.GitNotifyApplication;

import javax.inject.Inject;

/**
 * Created by relferreira on 1/23/17.
 */
public class AuthenticatorService extends Service {

    @Inject
    Authenticator autenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        ((GitNotifyApplication)getApplication()).getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return autenticator.getIBinder();
    }
}
