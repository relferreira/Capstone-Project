package com.relferreira.gitnotify.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.injector.ApplicationComponent;
import com.relferreira.gitnotify.ui.base.BaseActivity;
import com.relferreira.gitnotify.util.Navigator;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/5/17.
 */

public class DetailActivity extends BaseActivity {

    public static final String ARG_EVENT_ID = "arg_event_id";
    public static final String ARG_EVENT_TYPE = "arg_event_type";

    @Inject
    Navigator navigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        String eventId = getIntent().getStringExtra(ARG_EVENT_ID);
        String eventType = getIntent().getStringExtra(ARG_EVENT_TYPE);
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.detail_container, DetailFragment.newInstance(eventId, eventType, false))
                    .commit();

            supportPostponeEnterTransition();
        }

    }

    @Override
    public void injectActivity(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void redirectToLogin() {
        navigator.goToLogin(this);
    }
}
