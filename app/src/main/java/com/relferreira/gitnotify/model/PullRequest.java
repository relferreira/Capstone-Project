package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;

import javax.annotation.Nullable;

/**
 * Created by relferreira on 2/12/17.
 */
@Value.Immutable
public abstract class PullRequest {
    public abstract Integer id();
    public abstract Integer number();
    public abstract User user();
    public abstract String body();
    @SerializedName("created_at")
    public abstract Date createdAt();
    @SerializedName("updated_at")
    public abstract Date updatedAt();
    public abstract Integer commits();
    public abstract Integer additions();
    public abstract Integer deletions();
    @SerializedName("changed_files") @Nullable
    public abstract Integer changedFiles();
}
