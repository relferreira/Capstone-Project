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
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.AuthRepository;
import com.relferreira.gitnotify.repository.EventRepository;
import com.relferreira.gitnotify.repository.LogRepository;
import com.relferreira.gitnotify.repository.OrganizationRepository;
import com.relferreira.gitnotify.util.RequestErrorHelper;

import java.util.List;

import rx.schedulers.Schedulers;

/**
 * Created by relferreira on 1/25/17.
 */
public class EventsSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = EventsSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 30; // 30 minutes
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private Context context;
    private final AuthRepository authRepository;
    private final OrganizationRepository organizationRepository;
    private final EventRepository eventRepository;
    private final GithubService githubService;
    private final LogRepository Log;

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

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(LOG_TAG, "sync");
        githubService.listOrgs()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(organizations -> {
                    Log.i(LOG_TAG, "orgs");
                    organizationRepository.storeOrganizations(organizations);
                    loadEvents(account, organizations);
                }, error -> {
                    if(RequestErrorHelper.getCode(error) == 304) {
                        List<Organization> organizations = organizationRepository.listOrganizations();
                        loadEvents(account, organizations);
                    } else {
                        error.printStackTrace();
                        Log.e(LOG_TAG, "error retrieving organizations");
                    }
                });
    }

    public void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(authRepository.getAccount(),
                context.getString(R.string.content_authority), bundle);
    }

    private void loadEvents(Account account, List<Organization> organizations) {
        String username = authRepository.getUsername(account);

        loadPersonalEvents(username);
        for(Organization organization : organizations) {
            loadOrganizationEvents(username, organization);
        }
    }

    public void loadPersonalEvents(String username) {
        githubService.getEventsMe(username)
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(events -> {
                    eventRepository.storeEvents(events);
                }, error -> {
                    if(RequestErrorHelper.getCode(error) != 304) {
                        //TODO error management
                        error.printStackTrace();
                        Log.e(LOG_TAG, error.toString());
                    }
                });
    }

    public void loadOrganizationEvents(String username, Organization organization) {
        githubService.getEventsOrgs(username, organization.login())
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(events -> {
                    eventRepository.storeEvents(events);
                }, error -> {
                    if(RequestErrorHelper.getCode(error) != 304) {
                        //TODO error management
                        error.printStackTrace();
                        Log.e(LOG_TAG, error.toString());
                    }
                });
    }
}
