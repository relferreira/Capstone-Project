package com.relferreira.gitnotify.repository;

import android.content.SharedPreferences;

import com.relferreira.gitnotify.repository.interfaces.EtagRepository;

/**
 * Created by relferreira on 1/27/17.
 */
public class EtagSharedPreferencesRepository implements EtagRepository {

    private SharedPreferences sharedPreferences;

    public EtagSharedPreferencesRepository(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String getEtag(String key) {
        return sharedPreferences.getString(key, null);
    }

    @Override
    public void setEtag(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void invalidateCache() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
