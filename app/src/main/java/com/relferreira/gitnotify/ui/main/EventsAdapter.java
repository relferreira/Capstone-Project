package com.relferreira.gitnotify.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.util.CursorRecyclerViewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        String userImage = cursor.getString(cursor.getColumnIndex(EventColumns.ACTOR_IMAGE));
        Context context = viewHolder.userImageView.getContext();
        viewHolder.titleTextView.setText(title);
        viewHolder.subtitleTextView.setText(subtitle);
        Picasso.with(context)
                .load(String.format("%1$sv=3&s=60", userImage))
                .into(viewHolder.userImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable img = ((BitmapDrawable) viewHolder.userImageView.getDrawable());
                        viewHolder.userImageView.setImageDrawable(getRoundImage(img, context.getResources()));
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

    private RoundedBitmapDrawable getRoundImage(BitmapDrawable img, Resources resources) {
        Bitmap imageBitmap = img.getBitmap();
        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap);
        imageDrawable.setCircular(true);
        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
        return imageDrawable;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.event_user_image)
        ImageView userImageView;
        @BindView(R.id.event_title)
        TextView titleTextView;
        @BindView(R.id.event_subtitle)
        TextView subtitleTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
