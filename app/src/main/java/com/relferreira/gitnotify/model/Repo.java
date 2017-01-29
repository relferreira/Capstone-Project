package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/28/17.
 */
@Value.Immutable
public abstract class Repo {
    public abstract Integer id();
    public abstract String name();
    public abstract String url();
}
