package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

/**
 * Created by relferreira on 2/12/17.
 */
@Value.Immutable
public abstract class Page {
    @SerializedName("page_name")
    public abstract String pageName();
    public abstract String title();
    public abstract String action();
}
