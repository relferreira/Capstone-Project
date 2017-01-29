package com.relferreira.gitnotify.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;

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
    public void storeEvents(List<Event> events) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(events.size());
        for(Event event : events) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    GithubProvider.Events.CONTENT_URI);

            builder.withValue(EventColumns.ID, event.id());
            builder.withValue(EventColumns.TYPE, event.type());
            builder.withValue(EventColumns.ACTOR_ID, event.actor().id());
            builder.withValue(EventColumns.ACTOR_NAME, event.actor().displayLogin());
            builder.withValue(EventColumns.ACTOR_IMAGE, event.actor().avatarUrl());
            builder.withValue(EventColumns.REPO_ID, event.repo().id());
            builder.withValue(EventColumns.REPO_NAME, event.repo().name());
            builder.withValue(EventColumns.CREATED_AT, event.createdAt().getTime());
            builder.withValue(EventColumns.ORG_ID, event.org().id());
            builder.withValue(EventColumns.PAYLOAD, event.payload().toString());
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
}
