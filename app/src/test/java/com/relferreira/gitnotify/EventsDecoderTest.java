package com.relferreira.gitnotify;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableActor;
import com.relferreira.gitnotify.model.ImmutableEvent;
import com.relferreira.gitnotify.model.ImmutableOrganization;
import com.relferreira.gitnotify.model.ImmutableRepo;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.model.Repo;
import com.relferreira.gitnotify.repository.EventDbRepository;
import com.relferreira.gitnotify.repository.LogRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Created by relferreira on 1/29/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventsDecoderTest {

    @Mock
    Context context;

    @Mock
    LogRepository logRepository;

    private EventDbRepository eventRepository;
    private Organization organization;
    private ImmutableEvent.Builder eventBuilder;

    @Before
    public void setup() {
        organization = ImmutableOrganization.builder()
                .id(123)
                .login("GitNotify")
                .avatarUrl("")
                .reposUrl("https://test.com")
                .build();

        ImmutableActor actor = ImmutableActor.builder()
                .id(1234)
                .login("relferreira")
                .avatarUrl("")
                .displayLogin("relferreira")
                .url("")
                .gravatarId("")
                .build();

        eventBuilder = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .actor(actor)
                .repo(ImmutableRepo.builder().id(123).name("GitNotify/app").url("").build())
                .isPublic(true)
                .createdAt(new Date())
                .org(organization);
        eventRepository = new EventDbRepository(context, logRepository);
    }

    @Test
    public void shouldDecodePullRequestEvent() {
        JsonObject pullRequestObj = new JsonObject();
        pullRequestObj.addProperty("title", "fix last commit");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "opened");
        jsonObject.addProperty("number", 123);
        jsonObject.add("pull_request", pullRequestObj);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s %2$s pull request %3$s#%4$d").when(context).getString(any(Integer.class));
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PullRequestEvent");

        assertEquals("relferreira opened pull request GitNotify/app#123", decoder.getTitle());
        assertEquals("fix last commit", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePullRequestEventMerged() {
        JsonObject pullRequestObj = new JsonObject();
        pullRequestObj.addProperty("title", "fix last commit");
        pullRequestObj.addProperty("merged", true);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "closed");
        jsonObject.addProperty("number", 123);
        jsonObject.add("pull_request", pullRequestObj);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s %2$s pull request %3$s#%4$d").when(context).getString(R.string.action_pull_request);
        doReturn("closed").when(context).getString(R.string.action_closed);
        doReturn("merged").when(context).getString(R.string.action_merged);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PullRequestEvent");

        assertEquals("relferreira merged pull request GitNotify/app#123", decoder.getTitle());
        assertEquals("fix last commit", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePushEvent() {

        JsonArray commits = new JsonArray();
        JsonObject commit = new JsonObject();
        commit.addProperty("message", "fix unit test");
        commits.add(commit);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "refs/heads/master");
        jsonObject.add("commits", commits);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s pushed to %2$s at %3$s").when(context).getString(R.string.action_push);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PushEvent");

        assertEquals("relferreira pushed to master at GitNotify/app", decoder.getTitle());
        assertEquals("fix unit test", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePushEventWithMoreThanOneCommit() {

        JsonArray commits = new JsonArray();
        JsonObject commit = new JsonObject();
        commit.addProperty("message", "fix unit test");
        JsonObject commitTwo = new JsonObject();
        commitTwo.addProperty("message", "adding unit test");
        commits.add(commit);
        commits.add(commitTwo);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "refs/heads/master");
        jsonObject.add("commits", commits);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s pushed to %2$s at %3$s").when(context).getString(R.string.action_push);
        doReturn("%1$d commits").when(context).getString(R.string.action_push_multiple_commits);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PushEvent");

        assertEquals("relferreira pushed to master at GitNotify/app", decoder.getTitle());
        assertEquals("2 commits", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeCommentOnIssue() {
        JsonObject issueObj = new JsonObject();
        issueObj.addProperty("number", 123);
        JsonObject commentObj = new JsonObject();
        commentObj.addProperty("body", "The unit test is not working");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("issue", issueObj);
        jsonObject.add("comment", commentObj);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s commented on issue %2$s#%3$d").when(context).getString(R.string.action_commented_on_issue);

        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "IssueCommentEvent");
        assertEquals("relferreira commented on issue GitNotify/app#123", decoder.getTitle());
        assertEquals("The unit test is not working", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeCommentOnIssueAsPullRequest() {
        JsonObject issueObj = new JsonObject();
        issueObj.addProperty("number", 123);
        issueObj.add("pull_request", new JsonObject());
        JsonObject commentObj = new JsonObject();
        commentObj.addProperty("body", "The unit test is not working");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("issue", issueObj);
        jsonObject.add("comment", commentObj);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s commented on pull request %2$s#%3$d").when(context).getString(R.string.action_commented_on_pull_request);

        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "IssueCommentEvent");
        assertEquals("relferreira commented on pull request GitNotify/app#123", decoder.getTitle());
        assertEquals("The unit test is not working", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePullRequestReviewCommentEvent() {
        JsonObject pullRequestObj = new JsonObject();
        pullRequestObj.addProperty("number", 123);

        JsonObject commentObj = new JsonObject();
        commentObj.addProperty("body", "Check if unit test is passing");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("pull_request", pullRequestObj);
        jsonObject.add("comment", commentObj);
        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s commented on pull request %2$s#%3$d").when(context).getString(R.string.action_commented_on_pull_request);

        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PullRequestReviewCommentEvent");
        assertEquals("relferreira commented on pull request GitNotify/app#123", decoder.getTitle());
        assertEquals("Check if unit test is passing", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeCommitCommentEvent() {

        JsonObject commentObj = new JsonObject();
        commentObj.addProperty("body", "Check if unit test is passing");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("comment", commentObj);
        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s commented on commit %2$s").when(context).getString(R.string.action_commented_on_commit);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "CommitCommentEvent");
        assertEquals("relferreira commented on commit GitNotify/app", decoder.getTitle());
        assertEquals("Check if unit test is passing", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeCreateEvent() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "espresso_tests");
        jsonObject.addProperty("ref_type", "branch");
        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s created %2$s %3$s at %4$s").when(context).getString(R.string.action_create_event);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "CreateEvent");
        assertEquals("relferreira created branch espresso_tests at GitNotify/app", decoder.getTitle());
        assertEquals(null, decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeDeleteEvent() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "espresso_tests");
        jsonObject.addProperty("ref_type", "branch");
        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s deleted %2$s %3$s at %4$s").when(context).getString(R.string.action_deleted_event);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "DeleteEvent");
        assertEquals("relferreira deleted branch espresso_tests at GitNotify/app", decoder.getTitle());
        assertEquals(null, decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeStarring() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "started");
        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s starred %2$s").when(context).getString(R.string.action_starred);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "WatchEvent");
        assertEquals("relferreira starred GitNotify/app", decoder.getTitle());
        assertEquals(null, decoder.getSubtitle());
    }

    @Test
    public void shouldDecodeForkEvent() {
        JsonObject fork = new JsonObject();
        fork.addProperty("full_name", "GitNotify/app");
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("forkee", fork);

        Repo originalRepo = ImmutableRepo.builder().id(123).name("test/app").url("").build();
        Event event = eventBuilder
                .repo(originalRepo)
                .payload(jsonObject)
                .build();

        doReturn("%1$s forked %2$s to %3$s").when(context).getString(R.string.action_fork);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "ForkEvent");
        assertEquals("relferreira forked test/app to GitNotify/app", decoder.getTitle());
    }


}
