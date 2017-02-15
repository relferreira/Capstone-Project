package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Wiki;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public class WikiDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public WikiDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        JsonArray pages = payload.getAsJsonArray("pages");
        String action = pages.get(0).getAsJsonObject().get("action").getAsString();
        String repo = event.repo().name();
        return String.format(context.getString(R.string.action_wiki), actor, action, repo);
    }

    @Override
    public String getSubtitle() {
        JsonArray pages = payload.getAsJsonArray("pages");
        String action = pages.get(0).getAsJsonObject().get("action").getAsString();
        action = action.substring(0, 1).toUpperCase() + action.substring(1, action.length());
        if(pages.size() > 1)
            return String.format(context.getString(R.string.action_wiki_subtitle_multiple), action, pages.size());
        else {
            String title = pages.get(0).getAsJsonObject().get("title").getAsString();
            return String.format(context.getString(R.string.action_wiki_subtitle), action, title);
        }
    }

    @Override
    public String getDetailTitle() {
        return null;
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        Wiki wiki = interactor.constructWiki(payload);
        listener.successLoadingData(wiki.pages());
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {

    }
}