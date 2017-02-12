package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created by relferreira on 1/10/17.
 */
@Value.Immutable
public abstract class User {
    public abstract String login();
    public abstract String url();
    @SerializedName("avatar_url") @Nullable
    public abstract String avatarUrl();
}