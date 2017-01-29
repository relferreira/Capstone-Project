package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created by relferreira on 1/28/17.
 */
@Value.Immutable
public abstract class Payload {
    @SerializedName("push_id")
    public abstract Integer pushId();
    public abstract Integer size();
    @SerializedName("distinct_size")
    public abstract Integer distinctSize();
    public abstract String ref();
    public abstract String head();
    public abstract String before();
    public abstract List<Commit> commits();
}
