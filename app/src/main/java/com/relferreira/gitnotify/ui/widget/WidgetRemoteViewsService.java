package com.relferreira.gitnotify.ui.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by relferreira on 2/15/17.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    public static final int LIMIT = 10;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() { }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                String[] projection = new String[]{EventColumns.ID, EventColumns.TITLE, EventColumns.ACTOR_IMAGE, EventColumns.TYPE};
                data = getContentResolver().query(
                        GithubProvider.Events.CONTENT_URI, projection, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                if (data == null) {
                    return 0;
                } else {
                    int size = data.getCount();
                    // Limit results in 10
                    return size > LIMIT ? LIMIT : size;
                }
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);

                String eventId = data.getString(data.getColumnIndex(EventColumns.ID));
                String eventType = data.getString(data.getColumnIndex(EventColumns.TYPE));
                String title = data.getString(data.getColumnIndex(EventColumns.TITLE));
                String userImage = data.getString(data.getColumnIndex(EventColumns.ACTOR_IMAGE));
                setRemoteContentDescription(views, getString(R.string.a11y_image));
                try {
                    Bitmap image = Picasso.with(WidgetRemoteViewsService.this)
                            .load(String.format(getString(R.string.profile_image_format), userImage))
                            .get();
                    views.setImageViewBitmap(R.id.widget_item_image, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                views.setTextViewText(R.id.widget_item_title, title);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MainActivity.ARG_EVENT_ID, eventId);
                fillInIntent.putExtra(MainActivity.ARG_EVENT_TYPE, eventType);
                // To mantain intent
                fillInIntent.setAction(Long.toString(System.currentTimeMillis()));
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
                return views;
            }

            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_item_image, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(EventColumns.ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}