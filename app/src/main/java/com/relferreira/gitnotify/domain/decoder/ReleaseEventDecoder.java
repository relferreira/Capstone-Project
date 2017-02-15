package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableReleaseInfo;
import com.relferreira.gitnotify.model.Release;
import com.relferreira.gitnotify.model.ReleaseInfo;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

import java.util.Collections;

/**
 * Created by relferreira on 2/5/17.
 */

public class ReleaseEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public ReleaseEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String repo = event.repo().name();
        String version = payload.getAsJsonObject("release").get("tag_name").getAsString();
        return String.format(context.getString(R.string.action_release), version, repo);
    }

    @Override
    public String getSubtitle() {
        String actor = event.actor().login();
        return String.format(context.getString(R.string.action_member_by), actor);
    }

    @Override
    public String getDetailTitle() {
        return context.getString(R.string.release_title);
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        Release release = interactor.constructRelease(payload.getAsJsonObject("release"));
        ReleaseInfo releaseInfo = ImmutableReleaseInfo.builder().release(release).repo(event.repo()).build();

        listener.successLoadingData(Collections.singletonList(releaseInfo));
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {

    }
}