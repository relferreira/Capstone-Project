package com.relferreira.gitnotify.ui.detail;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.ui.base.BaseDialogFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by relferreira on 2/6/17.
 */

public class DetailFragment extends BaseDialogFragment implements DetailView, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    public static final String ARG_EVENT_ID = "arg_event_id";
    public static final String ARG_TABLET_MODE = "arg_tablet_mode";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_profile)
    ImageView profileImageView;
    @BindView(R.id.detail_title)
    TextView titleTextView;
    @BindView(R.id.detail_subtitle)
    TextView subtitleTextView;

    private Unbinder unbinder;
    private String eventId;
    private boolean tabletMode;

    public static DetailFragment newInstance(String eventId, boolean tabletMode) {
        DetailFragment frag = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_EVENT_ID, eventId);
        bundle.putBoolean(ARG_TABLET_MODE, tabletMode);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        eventId = arguments.getString(ARG_EVENT_ID);
        tabletMode = arguments.getBoolean(ARG_TABLET_MODE);

        BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(tabletMode)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home && tabletMode){
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void injectFragment(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void showLoading(boolean state) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = EventColumns.ID + " = ? ";
        String[] selectionArgs = new String[] { eventId };
        return new CursorLoader(getActivity(), GithubProvider.Events.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            String title = data.getString(data.getColumnIndex(EventColumns.TITLE));
            String subtitle = data.getString(data.getColumnIndex(EventColumns.SUB_TITLE));
            String userImage = data.getString(data.getColumnIndex(EventColumns.ACTOR_IMAGE));

            titleTextView.setText(title);
            subtitleTextView.setText(subtitle);
            Picasso.with(getActivity())
                    .load(userImage)
                    .into(profileImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
