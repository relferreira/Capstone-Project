package com.relferreira.gitnotify.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

/**
 * Created by relferreira on 1/26/17.
 */
@Value.Immutable
public abstract class Organization {
    public abstract Integer id();
    @Nullable
    public abstract String login();
    @SerializedName("repos_url") @Nullable
    public abstract String reposUrl();
    @SerializedName("events_url")  @Nullable
    public abstract String eventsUrl();
    @SerializedName("hooks_url")  @Nullable
    public abstract String hooksUrl();
    @SerializedName("issues_url")  @Nullable
    public abstract String issuesUrl();
    @SerializedName("members_url")  @Nullable
    public abstract String membersUrl();
    @SerializedName("public_members_url") @Nullable
    public abstract String publicMembersUrl();
    @SerializedName("avatar_url")  @Nullable
    public abstract String avatarUrl();
    @SerializedName("gravatar_id")  @Nullable
    public abstract String gravatarId();
    @Nullable
    public abstract String description();
}
