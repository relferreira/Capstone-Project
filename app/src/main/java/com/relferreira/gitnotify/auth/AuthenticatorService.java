package com.relferreira.gitnotify.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by relferreira on 1/23/17.
 */
public class AuthenticatorService extends Service {

    private Authenticator autenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        autenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return autenticator.getIBinder();
    }
}
