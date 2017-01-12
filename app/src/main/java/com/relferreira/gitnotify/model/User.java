package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/10/17.
 */
@Value.Immutable
public abstract class User {
    public abstract String login();
    public abstract String url();
}