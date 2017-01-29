package com.relferreira.gitnotify;

import android.content.Context;

import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableEvent;
import com.relferreira.gitnotify.model.ImmutableOrganization;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.EventRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.util.MockHttpException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by relferreira on 1/28/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventsSyncTest {

    @Mock
    Context context;
    @Mock
    AuthRepository authRepository;
    @Mock
    GithubService githubService;
    @Mock
    OrganizationRepository organizationRepository;
    @Mock
    LogRepository logRepository;
    @Mock
    EventRepository eventRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EventsSyncAdapter eventsSyncAdapter;
    private Organization organization;

    @Before
    public void setUp() {
        organization = ImmutableOrganization.builder()
                .id(123)
                .login("GitNotify")
                .avatarUrl("")
                .reposUrl("https://test.com")
                .build();
        eventsSyncAdapter = new EventsSyncAdapter(context, authRepository, organizationRepository, eventRepository,
                githubService, logRepository, true);
    }

    @Test
    public void shouldUpdateOrganizationDb() {
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        when(githubService.listOrgs()).thenReturn(Observable.just(organizations));

        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(organizationRepository, atLeastOnce()).storeOrganizations(organizations);
    }

    @Test
    public void shouldNotUpdateOrganizationDbWhenDataDidNotChanged() {
        when(githubService.listOrgs()).thenReturn(Observable.error(new MockHttpException(304, "")));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(organizationRepository, never()).storeOrganizations(any());
    }

    @Test
    public void shouldFetchEventsWithOrgsFromNetwork() {
        List<Organization> organizations = new ArrayList<>();
        Event event = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .build();
        organizations.add(organization);
        when(githubService.listOrgs()).thenReturn(Observable.just(organizations));
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(organizationRepository.listOrganizations()).thenReturn(organizations);
        when(githubService.getOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(githubService, atLeastOnce()).getOrgs("relferreira", organization.login());
    }

    @Test
    public void shouldFetchEventsEvenIfOrgsOnCache() {
        List<Organization> organizations = new ArrayList<>();
        Event event = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .build();
        organizations.add(organization);
        when(githubService.listOrgs()).thenReturn(Observable.error(new MockHttpException(304, "")));
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(organizationRepository.listOrganizations()).thenReturn(organizations);
        when(githubService.getOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(githubService, atLeastOnce()).getOrgs("relferreira", organization.login());
    }

    @Test
    public void shouldStoreEventFromNetwork() {
        Event event = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .build();
        List<Event> events = Collections.singletonList(event);
        when(githubService.getOrgs("relferreira", organization.login())).thenReturn(Observable.just(events));
        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization);

        verify(eventRepository, atLeastOnce()).storeEvents(events);
    }

    @Test
    public void shouldNotStoreEventWhenNotModified() {
        Event event = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .build();
        List<Event> events = Collections.singletonList(event);
        when(githubService.getOrgs("relferreira", organization.login())).thenReturn(Observable.error(new MockHttpException(304, "")));
        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization);

        verify(eventRepository, never()).storeEvents(events);
    }
}
