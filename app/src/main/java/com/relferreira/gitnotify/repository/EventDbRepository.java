package com.relferreira.gitnotify.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

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
            }
            builder.withValue(EventColumns.USER_ORG, (event.isUserOrg() != null && event.isUserOrg()) ? 1 : 0);

            Repo repo = event.repo();
            if(repo != null) {
                builder.withValue(EventColumns.REPO_ID, repo.id());
                builder.withValue(EventColumns.REPO_NAME, repo.name());
            }

            builder.withValue(EventColumns.TITLE, event.title());
            builder.withValue(EventColumns.SUB_TITLE, event.subtitle());
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

    @Override
    public void removeEvents() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(GithubProvider.Events.CONTENT_URI, null, null);
    }
}
