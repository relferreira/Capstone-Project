package com.relferreira.gitnotify.repository.interfaces;

/**
 * Created by relferreira on 1/27/17.
 */
public interface EtagRepository {

    String getEtag(String key);

    void setEtag(String key, String value);

}
