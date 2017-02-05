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
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.model.Repo;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;
import com.relferreira.gitnotify.repository.interfaces.LogRepository;

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
    public void storeEvents(List<Event> events, List<Organization> organizations) {
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
            builder.withValue(EventColumns.CREATED_AT, event.createdAt().getTime());
            builder.withValue(EventColumns.PAYLOAD, event.payload().toString());

            Organization org = event.org();
            if(org != null) {
                builder.withValue(EventColumns.ORG_ID, org.id());
                builder.withValue(EventColumns.USER_ORG, checkIfIsUserOrganization(org, organizations));
            } else {
                builder.withValue(EventColumns.USER_ORG, false);
            }

            Repo repo = event.repo();
            if(repo != null) {
                builder.withValue(EventColumns.REPO_ID, repo.id());
                builder.withValue(EventColumns.REPO_NAME, repo.name());
            }

            DescriptionDecoder encoder = getDecoder(context, event, type);
            if(encoder != null){
                String title = encoder.getTitle();
                String subtitle = encoder.getSubtitle();
                builder.withValue(EventColumns.TITLE, title);
                builder.withValue(EventColumns.SUB_TITLE, subtitle);
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
            case "GollumEvent":
                return new WikiDecoder(context, event);
            case "IssuesEvent":
                return new IssuesEventDecoder(context, event);
            case "MemberEvent":
                return new MemberEventDecoder(context, event);
            case "PublicEvent":
                return new PublicEventDecoder(context, event);
            case "PullRequestReviewEvent":
                return new PullRequestReviewEventDecoder(context, event);
            case "ReleaseEvent":
                return new ReleaseEventDecoder(context, event);
        }
        return null;
    }

    private boolean checkIfIsUserOrganization(Organization org, List<Organization> organizations) {
        boolean isUserOrganization = false;
        for(Organization organization : organizations){
            if(organization.id().equals(org.id())) {
                isUserOrganization = true;
                break;
            }
        }
        return isUserOrganization;
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
            else if(commits.size() > 0)
                return commits.get(0).getAsJsonObject().get("message").getAsString();
            return null;
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

    public class WikiDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public WikiDecoder(Context context, Event event){
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
    }

    public class IssuesEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public IssuesEventDecoder(Context context, Event event){
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
    }

    public class MemberEventDecoder implements DescriptionDecoder {

        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public MemberEventDecoder(Context context, Event event){
            this.context = context;
            this.event = event;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            String repo = event.repo().name();
            String member = payload.getAsJsonObject("member").get("login").getAsString();
            String action = payload.get("action").getAsString();
            return String.format(context.getString(R.string.action_member), member, action, repo);
        }

        @Override
        public String getSubtitle() {
            String sender = payload.getAsJsonObject("sender").get("login").getAsString();
            return String.format(context.getString(R.string.action_member_by), sender);
        }
    }

    public class PublicEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public PublicEventDecoder(Context context, Event event){
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

    public class PullRequestReviewEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public PullRequestReviewEventDecoder(Context context, Event event){
            this.context = context;
            this.event = event;
            this.payload = event.payload();
        }

        @Override
        public String getTitle() {
            String actor = event.actor().login();
            String repo = event.repo().name();
            int number = payload.getAsJsonObject("pull_request").get("number").getAsInt();
            return String.format(context.getString(R.string.action_pull_request_review), actor, repo, number);
        }

        @Override
        public String getSubtitle() {
            return payload.getAsJsonObject("review").get("body").getAsString();
        }
    }

    public class ReleaseEventDecoder implements DescriptionDecoder {
        private final JsonObject payload;
        private final Context context;
        private final Event event;

        public ReleaseEventDecoder(Context context, Event event){
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
    }
}
