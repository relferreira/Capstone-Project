package com.relferreira.gitnotify;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableActor;
import com.relferreira.gitnotify.model.ImmutableEvent;
import com.relferreira.gitnotify.model.ImmutableOrganization;
import com.relferreira.gitnotify.model.ImmutableRepo;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.AuthRepository;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;
import com.relferreira.gitnotify.repository.interfaces.LogRepository;
import com.relferreira.gitnotify.repository.interfaces.OrganizationRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

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
    private Event event;

    @Before
    public void setUp() {
        organization = ImmutableOrganization.builder()
                .id(123)
                .login("GitNotify")
                .avatarUrl("")
                .reposUrl("https://test.com")
                .build();
        event = ImmutableEvent.builder()
                .id("123")
                .type("commit")
                .actor(ImmutableActor.builder().id(1234).login("relferreira").avatarUrl("").displayLogin("relferreira").url("").gravatarId("").build())
                .repo(ImmutableRepo.builder().id(123).name("").url("").build())
                .payload(new JsonObject())
                .isPublic(true)
                .createdAt(new Date())
                .org(organization)
                .build();
        eventsSyncAdapter = new EventsSyncAdapter(context, authRepository, organizationRepository, eventRepository,
                githubService, logRepository, true);
    }

    //TODO REFACTOR
//    @Test
//    public void shouldUpdateOrganizationDb() {
//        List<Organization> organizations = new ArrayList<>();
//        organizations.add(organization);
//        when(githubService.listOrgs()).thenReturn(Observable.just(organizations));
//        when(authRepository.getUsername(any())).thenReturn("relferreira");
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.just(Collections.singletonList(event)));
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
//        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
//        verify(organizationRepository, atLeastOnce()).storeOrganizations(organizations);
//    }
//
//    @Test
//    public void shouldNotUpdateOrganizationDbWhenDataDidNotChanged() {
//        List<Organization> organizations = new ArrayList<>();
//        organizations.add(organization);
//        when(githubService.listOrgs()).thenReturn(Observable.error(new MockHttpException(304, "")));
//        when(organizationRepository.listOrganizations()).thenReturn(organizations);
//        when(authRepository.getUsername(any())).thenReturn("relferreira");
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.just(Collections.singletonList(event)));
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
//        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
//        verify(organizationRepository, never()).storeOrganizations(any());
//    }
//
//    @Test
//    public void shouldFetchEventsWithOrgsFromNetwork() {
//        List<Organization> organizations = new ArrayList<>();
//        organizations.add(organization);
//        when(githubService.listOrgs()).thenReturn(Observable.just(organizations));
//        when(authRepository.getUsername(any())).thenReturn("relferreira");
//        when(organizationRepository.listOrganizations()).thenReturn(organizations);
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.just(Collections.singletonList(event)));
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
//        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
//        verify(githubService, atLeastOnce()).getEventsOrgs("relferreira", organization.login());
//    }
//
//    @Test
//    public void shouldFetchEventsEvenIfOrgsOnCache() {
//        List<Organization> organizations = new ArrayList<>();
//        organizations.add(organization);
//        when(githubService.listOrgs()).thenReturn(Observable.error(new MockHttpException(304, "")));
//        when(authRepository.getUsername(any())).thenReturn("relferreira");
//        when(organizationRepository.listOrganizations()).thenReturn(organizations);
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.just(Collections.singletonList(event)));
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.just(Collections.singletonList(event)));
//        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
//        verify(githubService, atLeastOnce()).getEventsOrgs("relferreira", organization.login());
//    }
//
//    @Test
//    public void shouldStoreEventFromNetwork() {
//        List<Event> events = Collections.singletonList(event);
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.just(events));
//        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization);
//
//        verify(eventRepository, atLeastOnce()).storeEvents(events);
//    }
//
//    @Test
//    public void shouldNotStoreEventWhenNotModified() {
//        List<Event> events = Collections.singletonList(event);
//        when(githubService.getEventsOrgs("relferreira", organization.login())).thenReturn(Observable.error(new MockHttpException(304, "")));
//        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization);
//
//        verify(eventRepository, never()).storeEvents(events);
//    }
//
//    @Test
//    public void shouldStorePersonalEventFromNetwork() {
//        List<Event> events = Collections.singletonList(event);
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.just(events));
//        eventsSyncAdapter.loadPersonalEvents("relferreira");
//
//        verify(eventRepository, atLeastOnce()).storeEvents(events);
//    }
//
//    @Test
//    public void shouldNotStorePersonalEventWhenNotModified() {
//        List<Event> events = Collections.singletonList(event);
//        when(githubService.getEventsMe("relferreira")).thenReturn(Observable.error(new MockHttpException(304, "")));
//        eventsSyncAdapter.loadPersonalEvents("relferreira");
//
//        verify(eventRepository, never()).storeEvents(events);
//    }
}
