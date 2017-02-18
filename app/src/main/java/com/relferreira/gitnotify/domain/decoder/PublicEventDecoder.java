package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

import java.util.Collections;

/**
 * Created by relferreira on 2/5/17.
 */

public class PublicEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public PublicEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String repo = payload.getAsJsonObject("repository").get("full_name").getAsString();
        return String.format(context.getString(R.string.action_public), repo);
    }

    @Override
    public String getSubtitle() {
        String sender = event.actor().login();
        return String.format(context.getString(R.string.action_member_by), sender);
    }

    @Override
    public String getDetailTitle() {
        return context.getString(R.string.public_title);
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        listener.successLoadingData(Collections.singletonList(String.format(context.getString(R.string.action_member_detail), getTitle(), getSubtitle())));
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {

    }
}