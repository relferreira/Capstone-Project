package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;

/**
 * Created by relferreira on 2/13/17.
 */
@Value.Immutable
public abstract class Release {
    public abstract String url();
    public abstract Integer id();
    @SerializedName("tag_name")
    public abstract String tagName();
    public abstract Boolean prerelease();
    @SerializedName("published_at")
    public abstract Date publishedAt();
    public abstract String body();
}
