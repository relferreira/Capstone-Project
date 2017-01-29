package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/28/17.
 */
@Value.Immutable
public abstract class Author {
    public abstract String email();
    public abstract String name();
}
