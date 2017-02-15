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

public class IssueCommentEventDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public IssueCommentEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = (event != null) ? event.payload() : null; //TODO return
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        JsonObject issue = payload.getAsJsonObject("issue");
        int number = issue.get("number").getAsInt();
        if(issue.has("pull_request"))
            return String.format(context.getString(R.string.action_commented_on_pull_request), actor, repo, number);
        else
            return String.format(context.getString(R.string.action_commented_on_issue), actor, repo, number);
    }

    @Override
    public String getSubtitle() {
        return payload.getAsJsonObject("comment").get("body").getAsString();
    }

    @Override
    public String getDetailTitle() {
        JsonObject issue = payload.getAsJsonObject("issue");
        return issue.get("title").getAsString();
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider,
                         DecoderListener listener) {
        getPage(interactor, event, schedulerProvider, listener, 1);
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {
        listener.showPageLoading(true);
        getPage(interactor, event, schedulerProvider, listener, page);
    }

    private void getPage(GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider,
                         DecoderListener listener, Integer page) {
        JsonObject issue = payload.getAsJsonObject("issue");
        String[] repoName = event.repo().name().split("/");

        interactor.getIssueComments(repoName[0], repoName[1], issue.get("number").getAsInt(), page)
                .compose(schedulerProvider.applySchedulers())
                .subscribe(response -> {
                    listener.successLoadingData(response);
                }, error -> {
                    listener.errorLoadingData(error.getMessage());
                });
    }

}