package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/15/17.
 */
@Value.Immutable
public abstract class Login {
    public abstract String id();
    public abstract String url();
    public abstract String token();
}
