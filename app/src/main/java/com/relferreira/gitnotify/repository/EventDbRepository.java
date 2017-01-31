package com.relferreira.gitnotify.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by relferreira on 1/28/17.
 */
public class EventDbRepository implements EventRepository {

    private static final String LOG_TAG = EventDbRepository.class.getSimpleName();
    private static final int MAX_DAYS_OF_STORAGE = 90;
    private Context context;
    private LogRepository Log;

    public interface DescriptionDecoder {
        String getTitle();
        String getSubtitle();
    }

    public EventDbRepository(Context context, LogRepository logRepository){
        this.context = context;
        Log = logRepository;
    }

    @Override
    public void storeEvents(List<Event> events) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(events.size());
        for(Event event : events) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    GithubProvider.Events.CONTENT_URI);

            String type = event.type();
            builder.withValue(EventColumns.ID, event.id());
            builder.withValue(EventColumns.TYPE, type);
            builder.withValue(EventColumns.ACTOR_ID, event.actor().id());
            builder.withValue(EventColumns.ACTOR_NAME, event.actor().displayLogin());
            builder.withValue(EventColumns.ACTOR_IMAGE, event.actor().avatarUrl());
            builder.withValue(EventColumns.REPO_ID, event.repo().id());
            builder.withValue(EventColumns.REPO_NAME, event.repo().name());
            builder.withValue(EventColumns.CREATED_AT, event.createdAt().getTime());
            builder.withValue(EventColumns.ORG_ID, event.org().id());
            builder.withValue(EventColumns.PAYLOAD, event.payload().toString());

            DescriptionDecoder encoder = getDecoder(context, event, type);
            if(encoder != null){
                builder.withValue(EventColumns.TITLE, encoder.getTitle());
                builder.withValue(EventColumns.SUB_TITLE, encoder.getSubtitle());
            }
            batchOperations.add(builder.build());
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.applyBatch(GithubProvider.AUTHORITY, batchOperations);

            // Remove events that are more than 90 day old (Github restriction)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -MAX_DAYS_OF_STORAGE);
            contentResolver.delete(GithubProvider.Events.CONTENT_URI,
                    EventColumns.CREATED_AT + " < ?",
                    new String[] { String.valueOf(cal.getTimeInMillis()) });

        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert" + e);
        }
    }

    public DescriptionDecoder getDecoder(Context context, Event event, String type){
        switch (type){
            case "PullRequestEvent":
                return new PullRequestDecoder(context, event);
            case "PushEvent":
                return new PushEventDecoder(context, event);
            case "IssueCommentEvent":
                return new IssueCommentEventDecoder(context, event);
            case "PullRequestReviewCommentEvent":
                return new PullRequestReviewCommentEventDecoder(context, event);
            case "CommitCommentEvent":
                return new CommitCommentEventDecoder(context, event);
            case "CreateEvent":
                return new CreateDeleteEventDecoder(context, event, true);
            case "DeleteEvent":
                return new CreateDeleteEventDecoder(context, event, false);
            case "WatchEvent":
                return new StarredDecoder(context, event);
            case "ForkEvent":
                return new ForkEventDecoder(context, event);
        }
        return null;
    }

    private class PullRequestDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public PullRequestDecoder(Context context, Event event){
            this.context = context;
            this.event = event;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            JsonObject payload = event.payload();
            String actor = event.actor().displayLogin();
            String action = payload.get("action").getAsString();
            String repo = event.repo().name();
            int number = payload.get("number").getAsInt();
            if(action.equals(context.getString(R.string.action_closed)) &&
                    payload.getAsJsonObject("pull_request").get("merged").getAsBoolean())
                action = context.getString(R.string.action_merged);
            return String.format(context.getString(R.string.action_pull_request), actor, action, repo, number);
        }

        @Override
        public String getSubtitle() {
            return payload.getAsJsonObject("pull_request").get("title").getAsString();
        }
    }

    public class PushEventDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public PushEventDecoder(Context context, Event event){
            this.context = context;
            this.event = event;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            String actor = event.actor().displayLogin();
            String[] branchRef = payload.get("ref").getAsString().split("/");
            String branch = branchRef[branchRef.length - 1];
            String repo = event.repo().name();

            return String.format(context.getString(R.string.action_push), actor, branch, repo);
        }

        @Override
        public String getSubtitle() {
            JsonArray commits = payload.getAsJsonArray("commits");
            if(commits.size() > 1)
                return String.format(context.getString(R.string.action_push_multiple_commits), commits.size());
            else
                return commits.get(0).getAsJsonObject().get("message").getAsString();
        }
    }

    public class IssueCommentEventDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public IssueCommentEventDecoder(Context context, Event event){
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

    public class PullRequestReviewCommentEventDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public PullRequestReviewCommentEventDecoder(Context context, Event event){
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
    }

    public class CommitCommentEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public CommitCommentEventDecoder(Context context, Event event){
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
    }

    public class CreateDeleteEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;
        private final boolean create;

        public CreateDeleteEventDecoder(Context context, Event event, boolean create){
            this.context = context;
            this.event = event;
            this.create = create;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            String actor = event.actor().displayLogin();
            String repo = event.repo().name();
            String ref = payload.get("ref").getAsString();
            String refType = payload.get("ref_type").getAsString();
            if(create)
                return String.format(context.getString(R.string.action_create_event), actor, refType, ref, repo);
            else
                return String.format(context.getString(R.string.action_deleted_event), actor, refType, ref, repo);
        }

        @Override
        public String getSubtitle() {
            return null;
        }
    }

    public class StarredDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public StarredDecoder(Context context, Event event){
            this.context = context;
            this.event = event;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            String actor = event.actor().displayLogin();
            String repo = event.repo().name();
            return String.format(context.getString(R.string.action_starred), actor, repo);
        }

        @Override
        public String getSubtitle() {
            return null;
        }
    }

    public class ForkEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public ForkEventDecoder(Context context, Event event){
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
}
