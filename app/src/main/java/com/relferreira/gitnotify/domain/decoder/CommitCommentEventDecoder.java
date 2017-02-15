package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public class CommitCommentEventDecoder implements DescriptionDecoder {
    private final JsonObject payload;
    private final StringRepository context;
    private final Event event;

    public CommitCommentEventDecoder(StringRepository context, Event event){
        this.context = context;
        this.event = event;
        this.payload = event.payload();
    }

    @Override
    public String getTitle() {
        String actor = event.actor().displayLogin();
        String repo = event.repo().name();
        return String.format(context.getString(R.string.action_commented_on_commit), actor, repo);
    }

    @Override
    public String getSubtitle() {
        return payload.getAsJsonObject("comment").get("body").getAsString();
    }

    @Override
    public String getDetailTitle() {
        return context.getString(R.string.commit_comment_title);
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
        Comment comment = interactor.constructComment(payload.getAsJsonObject("comment"));
        String[] repoName = event.repo().name().split("/");

        interactor.getCommitComments(repoName[0], repoName[1], comment.commitId(), page)
                .compose(schedulerProvider.applySchedulers())
                .subscribe(response -> {
                    listener.successLoadingData(response);
                }, error -> {
                    listener.errorLoadingData(error.getMessage());
                });
    }
}