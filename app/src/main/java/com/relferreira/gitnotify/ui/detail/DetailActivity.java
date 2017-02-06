package com.relferreira.gitnotify.ui.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/5/17.
 */

public class DetailActivity extends BaseActivity implements DetailView, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_EVENT_ID = "arg_event_id";
    private static final int LOADER_ID = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_profile)
    ImageView profileImageView;
    @BindView(R.id.detail_title)
    TextView titleTextView;
    @BindView(R.id.detail_subtitle)
    TextView subtitleTextView;

    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        eventId = getIntent().getStringExtra(ARG_EVENT_ID);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        Log.i("teste", "teste");
    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = EventColumns.ID + " = ? ";
        String[] selectionArgs = new String[] { eventId };
        return new CursorLoader(this, GithubProvider.Events.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            String title = data.getString(data.getColumnIndex(EventColumns.TITLE));
            String subtitle = data.getString(data.getColumnIndex(EventColumns.SUB_TITLE));
            String userImage = data.getString(data.getColumnIndex(EventColumns.ACTOR_IMAGE));

            titleTextView.setText(title);
            subtitleTextView.setText(subtitle);
            Picasso.with(this)
                    .load(userImage)
                    .into(profileImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
