package com.relferreira.gitnotify.ui.main;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by relferreira on 10/29/16.
 */
public class GitNotificationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_ORG_ID = "arg_org_id";
    private static final String ARG_LIST_ORGS = "arg_list_orgs";
    private static final String ARG_IS_ORG = "arg_is_org";
    private static final int LOADER_ID = 2;
    private int orgId;
    private EventsAdapter adapter;
    private Cursor data;
    private RecyclerView eventsList;
    private ArrayList<Integer> listOrgs;
    private boolean isOrg;

    public static GitNotificationsFragment newInstance(Integer orgId, ArrayList<Integer> listOrgs, boolean isOrg){
        GitNotificationsFragment frag = new GitNotificationsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ORG_ID, orgId);
        bundle.putIntegerArrayList(ARG_LIST_ORGS, listOrgs);
        bundle.putBoolean(ARG_IS_ORG, isOrg);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_notifications, container, false);
        orgId = getArguments().getInt(ARG_ORG_ID);
        listOrgs = getArguments().getIntegerArrayList(ARG_LIST_ORGS);
        isOrg = getArguments().getBoolean(ARG_IS_ORG);

        adapter = new EventsAdapter(getContext(), null);
        eventsList = (RecyclerView) view.findViewById(R.id.events_list);
        eventsList.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String[] selectionArgs;
        if(isOrg) {
            selection = EventColumns.ORG_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(listOrgs.get(orgId))};
        } else {
           List<String> listOrgsString = new ArrayList<>();
            for(int orgId : listOrgs)
                listOrgsString.add(String.valueOf(orgId));
            selection = String.format("%1$s NOT IN (%2$s) OR %1$s IS NULL", EventColumns.ORG_ID, TextUtils.join(", ", listOrgsString));
            selectionArgs = null;
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
}
