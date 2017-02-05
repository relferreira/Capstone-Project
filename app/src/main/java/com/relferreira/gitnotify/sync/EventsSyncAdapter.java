package com.relferreira.gitnotify.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.EventRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;
import com.relferreira.gitnotify.util.RequestException;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by relferreira on 1/25/17.
 */
public class EventsSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = EventsSyncAdapter.class.getSimpleName();
    public static final int STATUS_INIT = 4;
    public static final int STATUS_PROGRESS = 1;
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = 2;
    public static final int SYNC_INTERVAL = 60 * 30; // 30 minutes
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private final AuthRepository authRepository;
    private final OrganizationRepository organizationRepository;
    private final EventRepository eventRepository;
    private final GithubService githubService;
    private final LogRepository Log;

    private Context context;
    private PublishSubject<Integer> subject = PublishSubject.create();

    public EventsSyncAdapter(Context context, AuthRepository authRepository, OrganizationRepository organizationRepository,
                             EventRepository eventRepository, GithubService githubService, LogRepository logRepository, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        this.authRepository = authRepository;
        this.organizationRepository = organizationRepository;
        this.eventRepository = eventRepository;
        this.githubService = githubService;
        this.Log = logRepository;
    }

    public static void onAccountCreated(Account newAccount, Context context) {

        EventsSyncAdapter.configurePeriodicSync(context, newAccount, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    private static void configurePeriodicSync(Context context, Account account, int syncInterval, int flexTime) {
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public Observable<Integer> syncImmediately(Context context) {
        Account account = authRepository.getAccount();
        String authority = context.getString(R.string.content_authority);
        if(!ContentResolver.isSyncActive(account, authority)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account,
                    authority, bundle);
        }
        if(subject.hasCompleted() || subject.hasThrowable())
            subject = PublishSubject.create();
        return subject;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(LOG_TAG, "sync");
        subject.onNext(STATUS_INIT);
        try {
            sync(account);
            subject.onNext(STATUS_SUCCESS);
            subject.onCompleted();
        } catch (RequestException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
            subject.onError(e);
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            subject.onCompleted();
        }
    }

    private void sync(Account account) throws IOException, RequestException {
        subject.onNext(STATUS_PROGRESS);
        Response<List<Organization>> orgsResponse = githubService.listOrgsSync().execute();
        if(orgsResponse.isSuccessful()){
            organizationRepository.storeOrganizations(orgsResponse.body());
            loadEvents(account, orgsResponse.body());
        } else {
            if(orgsResponse.code() == 304) {
                List<Organization> organizations = organizationRepository.listOrganizations();
                loadEvents(account, organizations);
            } else {
                throw new RequestException(orgsResponse.errorBody().string());
            }
        }
    }

    private void loadEvents(Account account, List<Organization> organizations) throws IOException, RequestException {
        String username = authRepository.getUsername(account);
        loadPersonalEvents(username, organizations);
        for(Organization organization : organizations) {
            loadOrganizationEvents(username, organization, organizations);
        }
    }

    public void loadPersonalEvents(String username, List<Organization> organizations) throws IOException, RequestException {
        subject.onNext(STATUS_PROGRESS);
        Response<List<Event>> eventsMeRequest = githubService.getEventsMeSync(username).execute();
        if(eventsMeRequest.isSuccessful()){
            eventRepository.storeEvents(eventsMeRequest.body(), organizations);
        } else {
            if(eventsMeRequest.code() != 304)
                throw new RequestException(eventsMeRequest.errorBody().string());
        }
    }

    public void loadOrganizationEvents(String username, Organization organization, List<Organization> organizations) throws IOException, RequestException {
        subject.onNext(STATUS_PROGRESS);
        Response<List<Event>> eventsOrgRequest = githubService.getEventsOrgsSync(username, organization.login()).execute();
        if(eventsOrgRequest.isSuccessful()){
            eventRepository.storeEvents(eventsOrgRequest.body(), organizations);
        } else {
            if(eventsOrgRequest.code() != 304)
                throw new RequestException(eventsOrgRequest.errorBody().string());
        }
    }
}
