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

public class PullRequestDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public PullRequestDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        JsonObject payload = event.payload();
        String actor = event.actor().displayLogin();
        String action = payload.get("action").getAsString();
        String repo = event.repo().name();
        int number = payload.get("number").getAsInt();
        if(action.equals(context.getString(R.string.action_closed)) &&
                payload.getAsJsonObject("pull_request").get("merged").getAsBoolean())
            action = context.getString(R.string.action_merged);
        return String.format(context.getString(R.string.action_pull_request), actor, action, repo, number);
    }

    @Override
    public String getSubtitle() {
        return payload.getAsJsonObject("pull_request").get("title").getAsString();
    }

    @Override
    public String getDetailTitle() {
        return null;
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {

    }
}