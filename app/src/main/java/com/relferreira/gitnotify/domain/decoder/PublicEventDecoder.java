package com.relferreira.gitnotify.domain.decoder;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

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
        String sender = payload.getAsJsonObject("sender").get("login").getAsString();
        return String.format(context.getString(R.string.action_member_by), sender);
    }
}