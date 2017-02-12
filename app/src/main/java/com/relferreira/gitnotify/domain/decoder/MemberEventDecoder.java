package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public class MemberEventDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public MemberEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String repo = event.repo().name();
        String member = payload.getAsJsonObject("member").get("login").getAsString();
        String action = payload.get("action").getAsString();
        return String.format(context.getString(R.string.action_member), member, action, repo);
    }

    @Override
    public String getSubtitle() {
        String sender = payload.getAsJsonObject("sender").get("login").getAsString();
        return String.format(context.getString(R.string.action_member_by), sender);
    }

    @Override
    public String getDetailTitle() {
        return null;
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {

    }
}