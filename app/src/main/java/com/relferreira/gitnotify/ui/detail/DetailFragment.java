package com.relferreira.gitnotify.ui.detail;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.JsonParser;
import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.model.Actor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableActor;
import com.relferreira.gitnotify.model.ImmutableEvent;
import com.relferreira.gitnotify.model.ImmutableOrganization;
import com.relferreira.gitnotify.model.ImmutableRepo;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.model.Repo;
import com.relferreira.gitnotify.repository.data.EventColumns;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.ui.base.BaseDialogFragment;
import com.relferreira.gitnotify.ui.pages.PagesAdapter;
import com.relferreira.gitnotify.ui.pages.PagesFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by relferreira on 2/6/17.
 */

public class DetailFragment extends BaseDialogFragment implements DetailView, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 3;
    public static final String ARG_EVENT_ID = "arg_event_id";
    public static final String ARG_EVENT_TYPE = "arg_event_type";
    public static final String ARG_TABLET_MODE = "arg_tablet_mode";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_profile)
    ImageView profileImageView;
    @BindView(R.id.detail_list)
    RecyclerView detailList;
    @BindView(R.id.detail_loading)
    ProgressBar loadingProgressBar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Inject
    DetailPresenter presenter;

    private Unbinder unbinder;
    private String eventId;
    private boolean tabletMode;
    private PagesAdapter adapter;
    private String eventType;
    private InfinityScrollListener scrollListener;
    private Event event;
    private boolean loading = false;

    public static DetailFragment newInstance(String eventId, String eventType, boolean tabletMode) {
        DetailFragment frag = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_EVENT_ID, eventId);
        bundle.putString(ARG_EVENT_TYPE, eventType);
        bundle.putBoolean(ARG_TABLET_MODE, tabletMode);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        eventId = arguments.getString(ARG_EVENT_ID);
        eventType = arguments.getString(ARG_EVENT_TYPE);
        tabletMode = arguments.getBoolean(ARG_TABLET_MODE);

        BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(tabletMode)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        adapter = PagesFactory.getAdapter(getContext(), new ArrayList<>(), eventType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        scrollListener = new InfinityScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(event != null) {
                    presenter.loadPage(getContext(), event, page);
                }
            }
        };
        detailList.setLayoutManager(layoutManager);
        detailList.setAdapter(adapter);
        detailList.addOnScrollListener(scrollListener);
        setHasOptionsMenu(true);

        presenter.attachView(this);
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
        presenter.dettachView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
        MenuItem refreshItem = menu.findItem(R.id.sync);
        refreshItem.setVisible(loading);

        if(loading) {
            Drawable drawable = refreshItem.getIcon();
            Animatable anim = ((Animatable) drawable);
            if(!anim.isRunning())
                anim.start();
        }
        super.onCreateOptionsMenu(menu, inflater);
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
        loadingProgressBar.setVisibility(state ? View.VISIBLE : View.GONE);
        loading = false;
        getActivity().invalidateOptionsMenu();
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
            event = fromCursor(data);
            presenter.getDecoder(getContext(), event);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void setActorImage(String image) {
        Picasso.with(getActivity())
                .load(image)
                .into(profileImageView);
    }

    @Override
    public void setTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    public void setAdapterData(List items) {
        adapter.setItems(items);
        showLoading(false);
    }

    @Override
    public void showError() {
        showLoading(false);
        //TODO
    }

    @Override
    public void showPageLoading(boolean status) {
        loading = status;
        getActivity().invalidateOptionsMenu();
    }

    private Event fromCursor(Cursor data) {
        ImmutableEvent.Builder eventBuilder = ImmutableEvent.builder();
        eventBuilder.id(data.getString(data.getColumnIndex(EventColumns.ID)));
        eventBuilder.type(data.getString(data.getColumnIndex(EventColumns.TYPE)));
        eventBuilder.actor(actorFromCursor(data));
        eventBuilder.createdAt(new Date(data.getLong(data.getColumnIndex(EventColumns.CREATED_AT))));
        eventBuilder.payload(new JsonParser().parse(data.getString(data.getColumnIndex(EventColumns.PAYLOAD))).getAsJsonObject());
        eventBuilder.org(organizationFromCursor(data));
        eventBuilder.repo(repoFromCursor(data));
        eventBuilder.title(data.getString(data.getColumnIndex(EventColumns.TITLE)));
        eventBuilder.subtitle(data.getString(data.getColumnIndex(EventColumns.SUB_TITLE)));

        return eventBuilder.build();
    }

    private Actor actorFromCursor(Cursor data) {
        return ImmutableActor.builder()
                .id(data.getInt(data.getColumnIndex(EventColumns.ACTOR_ID)))
                .login(data.getString(data.getColumnIndex(EventColumns.ACTOR_NAME)))
                .displayLogin(data.getString(data.getColumnIndex(EventColumns.ACTOR_NAME)))
                .avatarUrl(data.getString(data.getColumnIndex(EventColumns.ACTOR_IMAGE)))
                .build();
    }

    private Organization organizationFromCursor(Cursor data) {
        return ImmutableOrganization.builder().id(data.getInt(data.getColumnIndex(EventColumns.ORG_ID))).build();
    }

    private Repo repoFromCursor(Cursor data) {
        return ImmutableRepo.builder()
                .id(data.getInt(data.getColumnIndex(EventColumns.REPO_ID)))
                .name(data.getString(data.getColumnIndex(EventColumns.REPO_NAME)))
                .build();
    }
}
