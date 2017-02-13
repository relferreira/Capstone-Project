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

public class CreateDeleteEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;
    private final boolean create;

    public CreateDeleteEventDecoder(StringRepository context, Event event, boolean create){
        this.context = context;
        this.event = event;
        this.create = create;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        String ref = !payload.get("ref").isJsonNull() ? payload.get("ref").getAsString() : null;
        String refType = payload.get("ref_type").getAsString();
        if(ref == null || ref.isEmpty())
            return String.format(context.getString(R.string.action_create_repository), actor, repo);
        else if(create)
            return String.format(context.getString(R.string.action_create_event), actor, refType, ref, repo);
        else
            return String.format(context.getString(R.string.action_deleted_event), actor, refType, ref, repo);
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public String getDetailTitle() {
        return (create) ? context.getString(R.string.create_title) : context.getString(R.string.delete_title);
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        listener.successLoadingData(Collections.singletonList(getTitle()));
    }
}
