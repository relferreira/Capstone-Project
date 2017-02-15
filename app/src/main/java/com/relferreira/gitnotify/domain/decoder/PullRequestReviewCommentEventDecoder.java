package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableComment;
import com.relferreira.gitnotify.model.PullRequest;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public class PullRequestReviewCommentEventDecoder implements DescriptionDecoder {

    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public PullRequestReviewCommentEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        JsonObject issue = payload.getAsJsonObject("pull_request");
        int number = issue.get("number").getAsInt();
        return String.format(context.getString(R.string.action_commented_on_pull_request), actor, repo, number);
    }

    @Override
    public String getSubtitle() {
        return payload.getAsJsonObject("comment").get("body").getAsString();
    }

    @Override
    public String getDetailTitle() {
        return payload.getAsJsonObject("pull_request").get("title").getAsString();
    }

    @Override
    public void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener) {
        getPage(interactor, event, schedulerProvider, listener, 1);
    }

    @Override
    public void loadPage(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener, Integer page) {
        listener.showPageLoading(true);
        getPage(interactor, event, schedulerProvider, listener, page);
    }

    private void getPage(GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider,
                         DecoderListener listener, Integer page) {
        JsonObject pullRequest = payload.getAsJsonObject("pull_request");
        String[] repoName = event.repo().name().split("/");

        interactor.getPullComments(repoName[0], repoName[1], pullRequest.get("number").getAsInt(), page)
                .compose(schedulerProvider.applySchedulers())
                .subscribe(response -> {
                    if (page == 1) {
                        PullRequest pullRequestInfo = interactor.constructPullRequest(pullRequest);
                        Comment comment = ImmutableComment.builder()
                                .id(pullRequestInfo.id())
                                .user(pullRequestInfo.user())
                                .body(pullRequestInfo.body())
                                .createdAt(pullRequestInfo.createdAt())
                                .updatedAt(pullRequestInfo.updatedAt())
                                .build();
                        response.add(0, comment);
                    }
                    listener.successLoadingData(response);
                }, error -> {
                    listener.errorLoadingData(error.getMessage());
                });
    }
}
