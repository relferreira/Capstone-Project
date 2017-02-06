package com.relferreira.gitnotify.domain.decoder;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

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
        this.payload = event.payload();
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
}