package com.relferreira.gitnotify.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.util.CursorRecyclerViewAdapter;

/**
 * Created by relferreira on 1/31/17.
 */
public class EventsAdapter extends CursorRecyclerViewAdapter<EventsAdapter.EventViewHolder> {

    public EventsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(EventColumns.TITLE));
        String subtitle = cursor.getString(cursor.getColumnIndex(EventColumns.SUB_TITLE));

        viewHolder.titleTextView.setText(title);
        viewHolder.subtitleTextView.setText(subtitle);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_events, parent, false);
        return new EventViewHolder(itemView);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView subtitleTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.event_title);
            subtitleTextView = (TextView) itemView.findViewById(R.id.event_subtitle);
        }
    }
}
