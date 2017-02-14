package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 2/13/17.
 */
@Value.Immutable
public abstract class ReleaseInfo {
    public abstract Repo repo();
    public abstract Release release();
}
