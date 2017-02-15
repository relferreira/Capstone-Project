package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;

import javax.annotation.Nullable;

/**
 * Created by relferreira on 2/11/17.
 */
@Value.Immutable
public abstract class Comment {
    public abstract Integer id();
    @Nullable
    public abstract String url();
    @SerializedName("html_url") @Nullable
    public abstract String htmlUrl();
    public abstract User user();
    @SerializedName("created_at")
    public abstract Date createdAt();
    @SerializedName("updated_at")
    public abstract Date updatedAt();
    public abstract String body();
    @SerializedName("commit_id") @Nullable
    public abstract String commitId();

}
