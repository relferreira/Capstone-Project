package com.relferreira.gitnotify.repository;

import android.content.Context;

import com.relferreira.gitnotify.repository.interfaces.StringRepository;

/**
 * Created by relferreira on 2/5/17.
 */

public class StringContextRepository implements StringRepository {

    private final Context context;

    public StringContextRepository(Context context) {
        this.context = context;
    }

    @Override
    public String getString(int resId) {
        return context.getString(resId);
    }
}
