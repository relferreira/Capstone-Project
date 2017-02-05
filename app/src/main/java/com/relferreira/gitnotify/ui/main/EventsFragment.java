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

import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.base.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by relferreira on 10/29/16.
 */
public class EventsFragment extends BaseFragment implements EventsView, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_ORG_ID = "arg_org_id";
    private static final String ARG_LIST_ORGS = "arg_list_orgs";
    private static final String ARG_IS_ORG = "arg_is_org";
    private static final int LOADER_ID = 2;

    private int orgId;
    private EventsAdapter adapter;
    private Cursor data;
    private boolean isOrg;

    @BindView(R.id.events_refresh)
    SwipeRefreshLayout refreshList;
    @BindView(R.id.events_list)
    RecyclerView eventsList;

    @Inject
    EventsPresenter presenter;
    private Unbinder unbinder;

    public static EventsFragment newInstance(Integer orgId, boolean isOrg){
        EventsFragment frag = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ORG_ID, orgId);
        bundle.putBoolean(ARG_IS_ORG, isOrg);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_git_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);

        orgId = getArguments().getInt(ARG_ORG_ID);
        isOrg = getArguments().getBoolean(ARG_IS_ORG);

        adapter = new EventsAdapter(getContext(), null);
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
    public void showError() {
        MainActivity activity = (MainActivity) getActivity();
        activity.showError();
    }

}
