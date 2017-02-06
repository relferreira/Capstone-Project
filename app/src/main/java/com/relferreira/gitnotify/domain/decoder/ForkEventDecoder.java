package com.relferreira.gitnotify.domain.decoder;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

/**
 * Created by relferreira on 2/5/17.
 */

public class ForkEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public ForkEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        String toRepo = payload.getAsJsonObject("forkee").get("full_name").getAsString();
        return String.format(context.getString(R.string.action_fork), actor, repo, toRepo);
    }

    @Override
    public String getSubtitle() {
        return null;
    }
}
