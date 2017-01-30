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
                .repo(ImmutableRepo.builder().id(123).name("gitnotify").url("").build())
                .isPublic(true)
                .createdAt(new Date())
                .org(organization);
        eventRepository = new EventDbRepository(context, logRepository);
    }

    @Test
    public void shouldDecodePullRequestEvent() {
        JsonObject pullRequestObj = new JsonObject();
        pullRequestObj.addProperty("title", "fix last commit");

        JsonObject repoObj = new JsonObject();
        repoObj.addProperty("name", "gitnotify");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "opened");
        jsonObject.addProperty("number", 123);
        jsonObject.add("pull_request", pullRequestObj);
        jsonObject.add("repo", repoObj);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s %2$s pull request %3$s#%4$d").when(context).getString(any(Integer.class));
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PullRequestEvent");

        assertEquals("relferreira opened pull request gitnotify#123", decoder.getTitle());
        assertEquals("fix last commit", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePushEvent() {
        JsonObject repoObj = new JsonObject();
        repoObj.addProperty("name", "gitnotify");

        JsonArray commits = new JsonArray();
        JsonObject commit = new JsonObject();
        commit.addProperty("message", "fix unit test");
        commits.add(commit);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "refs/heads/master");
        jsonObject.add("repo", repoObj);
        jsonObject.add("commits", commits);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s pushed to %2$s at %3$s").when(context).getString(R.string.action_push);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PushEvent");

        assertEquals("relferreira pushed to master at gitnotify", decoder.getTitle());
        assertEquals("fix unit test", decoder.getSubtitle());
    }

    @Test
    public void shouldDecodePushEventWithMoreThanOneCommit() {
        JsonObject repoObj = new JsonObject();
        repoObj.addProperty("name", "gitnotify");

        JsonArray commits = new JsonArray();
        JsonObject commit = new JsonObject();
        commit.addProperty("message", "fix unit test");
        JsonObject commitTwo = new JsonObject();
        commitTwo.addProperty("message", "adding unit test");
        commits.add(commit);
        commits.add(commitTwo);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ref", "refs/heads/master");
        jsonObject.add("repo", repoObj);
        jsonObject.add("commits", commits);

        Event event = eventBuilder
                .payload(jsonObject)
                .build();

        doReturn("%1$s pushed to %2$s at %3$s").when(context).getString(R.string.action_push);
        doReturn("%1$d commits").when(context).getString(R.string.action_push_multiple_commits);
        EventDbRepository.DescriptionDecoder decoder = eventRepository.getDecoder(context, event, "PushEvent");

        assertEquals("relferreira pushed to master at gitnotify", decoder.getTitle());
        assertEquals("2 commits", decoder.getSubtitle());
    }
}
