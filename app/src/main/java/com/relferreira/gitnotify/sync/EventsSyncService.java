package com.relferreira.gitnotify.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.relferreira.gitnotify.GitNotifyApplication;

import javax.inject.Inject;

/**
 * Created by relferreira on 1/25/17.
 */
public class EventsSyncService extends Service {

    @Inject
    EventsSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        ((GitNotifyApplication)getApplication()).getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
