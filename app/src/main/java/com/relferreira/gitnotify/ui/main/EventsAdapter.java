package com.relferreira.gitnotify.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.util.CursorRecyclerViewAdapter;
import com.relferreira.gitnotify.util.RoundBitmapHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by relferreira on 1/31/17.
 */
public class EventsAdapter extends CursorRecyclerViewAdapter<EventsAdapter.EventViewHolder> {

    private final DateFormat dateFormater;
    protected final EventsAdapterListener listener;

    public interface EventsAdapterListener {
        void onSelect(int position);
    }

    public EventsAdapter(Context context, Cursor cursor, EventsAdapterListener listener) {
        super(context, cursor);
        this.listener = listener;

        dateFormater = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(EventColumns.TITLE));
        String subtitle = cursor.getString(cursor.getColumnIndex(EventColumns.SUB_TITLE));
        String userImage = cursor.getString(cursor.getColumnIndex(EventColumns.ACTOR_IMAGE));
        Long date = cursor.getLong(cursor.getColumnIndex(EventColumns.CREATED_AT));
        Context context = viewHolder.userImageView.getContext();

        viewHolder.dateTextView.setText(dateFormater.format(new Date(date)));
        viewHolder.titleTextView.setText(title);
        viewHolder.subtitleTextView.setText(subtitle);
        Picasso.with(context)
                .load(String.format("%1$sv=3&s=60", userImage))
                .into(viewHolder.userImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable img = ((BitmapDrawable) viewHolder.userImageView.getDrawable());
                        viewHolder.userImageView.setImageDrawable(RoundBitmapHelper.getRoundImage(img, context.getResources()));
                    }

                    @Override
                    public void onError() {
                        // TODO error image
                    }
                });
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_events, parent, false);
        return new EventViewHolder(itemView);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.event_item)
        RelativeLayout eventItem;
        @BindView(R.id.event_user_image)
        ImageView userImageView;
        @BindView(R.id.event_date)
        TextView dateTextView;
        @BindView(R.id.event_title)
        TextView titleTextView;
        @BindView(R.id.event_subtitle)
        TextView subtitleTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.event_item)
        public void select() {
            listener.onSelect(getAdapterPosition());
        }
    }
}
