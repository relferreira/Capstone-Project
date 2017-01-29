package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/28/17.
 */
@Value.Immutable
public abstract class Commit {
    public abstract String sha();
    public abstract Author author();
    public abstract String message();
    public abstract Boolean distinct();
    public abstract String url();
}
