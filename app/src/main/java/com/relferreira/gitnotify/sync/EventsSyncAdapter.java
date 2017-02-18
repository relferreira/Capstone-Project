package com.relferreira.gitnotify.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.domain.AuthInteractor;
import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.domain.OrganizationInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.LogRepository;
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

    public static final String ACTION_UPDATE_DATA = "com.relferreira.gitnotify.ACTION_DATA_UPDATED";
    public static final String LOG_TAG = EventsSyncAdapter.class.getSimpleName();
    public static final int STATUS_INIT = 4;
    public static final int STATUS_PROGRESS = 1;
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = 2;
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private final AuthInteractor authInteractor;
    private final OrganizationInteractor organizationInteractor;
    private final EventInteractor eventInteractor;
    private final GithubInteractor githubInteractor;
    private final LogRepository Log;

    private Context context;
    private PublishSubject<Integer> subject = PublishSubject.create();

    public EventsSyncAdapter(Context context, AuthInteractor authInteractor, OrganizationInteractor organizationInteractor,
                             EventInteractor eventInteractor, GithubInteractor githubInteractor, LogRepository logRepository, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        this.authInteractor = authInteractor;
        this.organizationInteractor = organizationInteractor;
        this.eventInteractor = eventInteractor;
        this.githubInteractor = githubInteractor;
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
        Account account = authInteractor.getAccount();
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
            broadcastSync();
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
        Response<List<Organization>> orgsResponse = githubInteractor.listOrgsSync();
        if(orgsResponse.isSuccessful()){
            organizationInteractor.storeOrganizations(orgsResponse.body());
            loadEvents(account, orgsResponse.body());
        } else {
            if(orgsResponse.code() == 304) {
                List<Organization> organizations = organizationInteractor.listOrganizations();
                loadEvents(account, organizations);
            } else {
                throw new RequestException(orgsResponse.errorBody().string());
            }
        }
    }

    private void loadEvents(Account account, List<Organization> organizations) throws IOException, RequestException {
        String username = authInteractor.getUsername(account);
        loadPersonalEvents(username, organizations);
        for(Organization organization : organizations) {
            loadOrganizationEvents(username, organization, organizations);
        }
    }

    public void loadPersonalEvents(String username, List<Organization> organizations) throws IOException, RequestException {
        subject.onNext(STATUS_PROGRESS);
        Response<List<Event>> eventsMeRequest = githubInteractor.getEventsMeSync(username);
        if(eventsMeRequest.isSuccessful()){
            eventInteractor.storeEvents(eventsMeRequest.body(), organizations);
        } else {
            if(eventsMeRequest.code() != 304)
                throw new RequestException(eventsMeRequest.errorBody().string());
        }
    }

    public void loadOrganizationEvents(String username, Organization organization, List<Organization> organizations) throws IOException, RequestException {
        subject.onNext(STATUS_PROGRESS);
        Response<List<Event>> eventsOrgRequest = githubInteractor.getEventsOrgsSync(username, organization.login());
        if(eventsOrgRequest.isSuccessful()){
            eventInteractor.storeEvents(eventsOrgRequest.body(), organizations);
        } else {
            if(eventsOrgRequest.code() != 304)
                throw new RequestException(eventsOrgRequest.errorBody().string());
        }
    }

    private void broadcastSync() {
        Intent dataUpdatedIntent = new Intent(ACTION_UPDATE_DATA);
        context.sendBroadcast(dataUpdatedIntent);
    }
}
