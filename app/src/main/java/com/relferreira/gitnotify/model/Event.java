package com.relferreira.gitnotify.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;

/**
 * Created by relferreira on 1/15/17.
 */
@Value.Immutable
public abstract class Event {
    public abstract String id();
    public abstract String type();
    public abstract Actor actor();
    public abstract Repo repo();
    public abstract JsonObject payload();
    @SerializedName("public")
    public abstract Boolean isPublic();
    @SerializedName("created_at")
    public abstract Date createdAt();
    public abstract Organization org();


}