package com.relferreira.gitnotify.repository;

/**
 * Created by relferreira on 1/28/17.
 */
public interface LogRepository {

    void i(String key, String value);

    void e(String key, String value);
}
