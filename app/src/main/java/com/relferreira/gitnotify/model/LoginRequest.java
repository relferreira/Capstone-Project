package com.relferreira.gitnotify.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created by relferreira on 1/15/17.
 */
@Value.Immutable
public abstract class LoginRequest {
    public abstract List<String> scopes();
    public abstract String note();
    @SerializedName("client_id")
    public abstract String clientId();
    @SerializedName("client_secret")
    public abstract String clientSecret();
}

