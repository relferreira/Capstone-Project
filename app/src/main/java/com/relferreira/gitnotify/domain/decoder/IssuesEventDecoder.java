package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Issue;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

import java.util.Collections;

/**
 * Created by relferreira on 2/5/17.
 */

public class IssuesEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public IssuesEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        JsonObject issue = payload.getAsJsonObject("issue");
        String action = payload.get("action").getAsString();
        int number = issue.get("number").getAsInt();
        String repo = event.repo().name();
        return String.format(context.getString(R.string.action_issue), actor, action, repo, number);
    }

    @Override
    public String getSubtitle() {
        JsonObject issue = payload.getAsJsonObject("issue");
        return issue.get("title").getAsString();
    }

    @Override
    public String getDetailTitle() {
        return context.getString(R.string.issues_title);
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        Issue issue = interactor.constructIssue(payload.getAsJsonObject("issue"));
        listener.successLoadingData(Collections.singletonList(issue));
    }
}
