package com.relferreira.gitnotify;

import android.content.Context;

import com.google.gson.JsonObject;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.domain.AuthInteractor;
import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.domain.OrganizationInteractor;
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
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;
import com.relferreira.gitnotify.util.RequestException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.mockito.Matchers.any;
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
    StringRepository stringRepository;
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
    @Mock
    GithubInteractor githubInteractor;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private EventsSyncAdapter eventsSyncAdapter;
    private Organization organization;
    private Event event;
    private AuthInteractor authInteractor;
    private OrganizationInteractor organizationInteractor;
    private EventInteractor eventInteractor;
    private Response<List<Organization>> responseNotModified;
    private okhttp3.Response rawResponse;
    private ResponseBody responseBody;

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
                .isUserOrg(true)
                .build();

        rawResponse = new okhttp3.Response.Builder() //
                .code(304)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost/").build())
                .build();
        responseBody = ResponseBody.create(MediaType.parse(""), "");
        responseNotModified = Response.error(responseBody, rawResponse);

        authInteractor = new AuthInteractor(authRepository);
        organizationInteractor = new OrganizationInteractor(organizationRepository);
        eventInteractor = new EventInteractor(stringRepository, eventRepository);
        eventsSyncAdapter = new EventsSyncAdapter(context, authInteractor, organizationInteractor, eventInteractor,
                githubInteractor, logRepository, true);
    }

    @Test
    public void shouldUpdateOrganizationDb() throws IOException {
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        when(githubInteractor.listOrgsSync()).thenReturn(Response.success(organizations));
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.success(Collections.singletonList(event)));
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.success(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(organizationRepository).storeOrganizations(organizations);
    }

    @Test
    public void shouldNotUpdateOrganizationDbWhenDataDidNotChanged() throws IOException {
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        when(githubInteractor.listOrgsSync()).thenReturn(responseNotModified);
        when(organizationRepository.listOrganizations()).thenReturn(organizations);
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.success(Collections.singletonList(event)));
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.success(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(organizationRepository, never()).storeOrganizations(any());
    }

    @Test
    public void shouldFetchEventsWithOrgsFromNetwork() throws IOException {
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        when(githubInteractor.listOrgsSync()).thenReturn(Response.success(organizations));
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(organizationRepository.listOrganizations()).thenReturn(organizations);
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.success(Collections.singletonList(event)));
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.success(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(githubInteractor).getEventsOrgsSync("relferreira", organization.login());
    }

    @Test
    public void shouldFetchEventsEvenIfOrgsOnCache() throws IOException {
        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        when(githubInteractor.listOrgsSync()).thenReturn(responseNotModified);
        when(authRepository.getUsername(any())).thenReturn("relferreira");
        when(organizationRepository.listOrganizations()).thenReturn(organizations);
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.success(Collections.singletonList(event)));
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.success(Collections.singletonList(event)));
        eventsSyncAdapter.onPerformSync(null, null, null, null, null);
        verify(githubInteractor).getEventsOrgsSync("relferreira", organization.login());
    }

    @Test
    public void shouldStoreEventFromNetwork() throws IOException, RequestException {

        List<Event> events = Collections.singletonList(event);
        List<Organization> organizations = Collections.singletonList(organization);
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.success(events));
        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization, organizations);

        verify(eventRepository).storeEvents(events, organizations);
    }

    @Test
    public void shouldNotStoreEventWhenNotModified() throws IOException, RequestException {
        List<Event> events = Collections.singletonList(event);
        List<Organization> organizations = Collections.singletonList(organization);
        when(githubInteractor.getEventsOrgsSync("relferreira", organization.login())).thenReturn(Response.error(responseBody, rawResponse));
        eventsSyncAdapter.loadOrganizationEvents("relferreira", organization, organizations);

        verify(eventRepository, never()).storeEvents(events, organizations);
    }

    @Test
    public void shouldStorePersonalEventFromNetwork() throws IOException, RequestException {
        List<Event> events = Collections.singletonList(event);
        List<Organization> organizations = Collections.singletonList(organization);
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.success(events));
        eventsSyncAdapter.loadPersonalEvents("relferreira", organizations);
        verify(eventRepository).storeEvents(events, organizations);
    }

    @Test
    public void shouldNotStorePersonalEventWhenNotModified() throws IOException, RequestException {
        List<Event> events = Collections.singletonList(event);
        List<Organization> organizations = Collections.singletonList(organization);
        when(githubInteractor.getEventsMeSync("relferreira")).thenReturn(Response.error(responseBody, rawResponse));
        eventsSyncAdapter.loadPersonalEvents("relferreira", organizations);

        verify(eventRepository, never()).storeEvents(events, organizations);
    }
}
