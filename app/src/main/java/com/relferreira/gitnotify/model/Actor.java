package com.relferreira.gitnotify.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/28/17.
 */
@Value.Immutable
public abstract class Actor {
    public abstract Integer id();
    public abstract String login();
    @SerializedName("display_login")
    public abstract String displayLogin();
    @SerializedName("gravatar_id") @Nullable
    public abstract String gravatarId();
    public abstract String url();
    @SerializedName("avatar_url") @Nullable
    public abstract String avatarUrl();
}
