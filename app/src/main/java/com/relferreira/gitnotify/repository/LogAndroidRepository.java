package com.relferreira.gitnotify.repository;

import android.util.Log;

import com.relferreira.gitnotify.repository.interfaces.LogRepository;

/**
 * Created by relferreira on 1/28/17.
 */
public class LogAndroidRepository implements LogRepository {

    @Override
    public void i(String key, String value) {
        Log.i(key, value);
    }

    @Override
    public void e(String key, String value) {
        Log.e(key, value);
    }
}
