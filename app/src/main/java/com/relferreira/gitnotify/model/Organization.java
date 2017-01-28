package com.relferreira.gitnotify.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Created by relferreira on 1/26/17.
 */
@Value.Immutable
public abstract class Organization {
    public abstract Integer id();
    public abstract String login();
    @SerializedName("repos_url")
    public abstract String reposUrl();
    @SerializedName("events_url")  @Nullable
    public abstract String eventsUrl();
    @SerializedName("hooks_url")  @Nullable
    public abstract String hooksUrl();
    @SerializedName("issues_url")  @Nullable
    public abstract String issuesUrl();
    @SerializedName("members_url")  @Nullable
    public abstract String membersUrl();
    @SerializedName("public_members_url")  @Nullable
    public abstract String publicMembersUrl();
    @SerializedName("avatar_url")  @Nullable
    public abstract String avatarUrl();
    @Nullable
    public abstract String description();
}
