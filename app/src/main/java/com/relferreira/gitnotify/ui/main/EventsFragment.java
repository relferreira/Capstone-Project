package com.relferreira.gitnotify.ui.main;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.base.BaseFragment;
import com.relferreira.gitnotify.util.AnalyticsTracker;
import com.relferreira.gitnotify.util.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by relferreira on 10/29/16.
 */
public class EventsFragment extends BaseFragment implements EventsView, EventsAdapter.EventsAdapterListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_ORG_ID = "arg_org_id";
    private static final String ARG_ORG_NAME = "arg_org_name";
    private static final String ARG_IS_ORG = "arg_is_org";
    private static final String ARG_TABLET_MODE = "arg_tablet_mode";
    private static final int LOADER_ID = 2;

    private int orgId;
    private EventsAdapter adapter;
    private Cursor data;
    private boolean isOrg;

    @BindView(R.id.events_refresh)
    SwipeRefreshLayout refreshList;
    @BindView(R.id.events_list)
    RecyclerView eventsList;
    @BindView(R.id.events_title) @javax.annotation.Nullable
    TextView eventsTitleTextView;

    @Inject
    EventsPresenter presenter;
    @Inject
    Navigator navigator;
    @Inject
    AnalyticsTracker tracker;

    private Unbinder unbinder;
    private Bundle arguments;
    private boolean tabletMode;
    private String orgName;

    public static EventsFragment newInstance(Integer orgId, String orgName, boolean isOrg, boolean tabletMode){
        EventsFragment frag = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ORG_ID, orgId);
        bundle.putString(ARG_ORG_NAME, orgName);
        bundle.putBoolean(ARG_IS_ORG, isOrg);
        bundle.putBoolean(ARG_TABLET_MODE, tabletMode);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        unbinder = ButterKnife.bind(this, view);

        arguments = getArguments();
        orgId = arguments.getInt(ARG_ORG_ID);
        orgName = arguments.getString(ARG_ORG_NAME);
        isOrg = arguments.getBoolean(ARG_IS_ORG);
        tabletMode = arguments.getBoolean(ARG_TABLET_MODE);

        adapter = new EventsAdapter(getContext(), null, this);
        eventsList.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsList.setAdapter(adapter);
        refreshList.setColorSchemeResources(R.color.colorAccent);
        refreshList.setOnRefreshListener(() -> {
            presenter.requestSync(getContext());
        });

        presenter.attachView(this);
        return view;
    }

    @Override
    public void injectFragment(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(presenter != null)
            presenter.dettachView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String[] selectionArgs;
        if(isOrg) {
            selection = EventColumns.ORG_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(orgId)};
        } else {
            selection = EventColumns.USER_ORG + " = ?";
            selectionArgs = new String[]{String.valueOf(0)};
        }
        return new CursorLoader(getActivity(), GithubProvider.Events.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.data = data;
        adapter.swapCursor(data);
        if(eventsTitleTextView != null)
            eventsTitleTextView.setText(orgName);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void showLoading(boolean state) {
        if(!state)
            refreshList.setRefreshing(false);
    }

    @Override
    public void redirectToLogin() {
        navigator.goToLogin(getActivity());
    }

    @Override
    public void showError() {
        MainActivity activity = (MainActivity) getActivity();
        activity.showError();
    }

    @Override
    public void onSelect(int position) {
        if(data.moveToPosition(position)){
            String eventId = data.getString(data.getColumnIndex(EventColumns.ID));
            String eventType = data.getString(data.getColumnIndex(EventColumns.TYPE));
            tracker.sendEventSelectionTrack(eventType);
            navigator.gotToDetails(eventId, eventType, getActivity(), getFragmentManager(), tabletMode);
        }
    }
}
