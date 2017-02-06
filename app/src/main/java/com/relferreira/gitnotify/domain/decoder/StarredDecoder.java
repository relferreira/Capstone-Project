package com.relferreira.gitnotify.domain.decoder;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

/**
 * Created by relferreira on 2/5/17.
 */

public class StarredDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public StarredDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        return String.format(context.getString(R.string.action_starred), actor, repo);
    }

    @Override
    public String getSubtitle() {
        return null;
    }
}