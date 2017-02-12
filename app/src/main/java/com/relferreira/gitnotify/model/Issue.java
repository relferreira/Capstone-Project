package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;

/**
 * Created by relferreira on 2/12/17.
 */
@Value.Immutable
public abstract class Issue {
    public abstract Integer id();
    public abstract Integer number();
    public abstract String title();
    public abstract User user();
    public abstract String state();
    @SerializedName("created_at")
    public abstract Date createdAt();
    @SerializedName("updated_at")
    public abstract Date updatedAt();
    public abstract String body();
}
