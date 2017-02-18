package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.repository.interfaces.EtagRepository;

/**
 * Created by relferreira on 2/17/17.
 */

public class CacheInteractor {

    private EtagRepository etagRepository;

    public CacheInteractor(EtagRepository etagRepository) {
        this.etagRepository = etagRepository;
    }

    public void invalidateCache() {
        etagRepository.invalidateCache();
    }
}
