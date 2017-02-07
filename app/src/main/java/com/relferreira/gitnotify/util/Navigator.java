package com.relferreira.gitnotify.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;

import com.relferreira.gitnotify.ui.detail.DetailActivity;
import com.relferreira.gitnotify.ui.detail.DetailFragment;
import com.relferreira.gitnotify.ui.login.LoginActivity;
import com.relferreira.gitnotify.ui.main.MainActivity;

/**
 * Created by relferreira on 1/28/17.
 */
public class Navigator {

    public void goToLogin(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    public void goToMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    public void gotToDetails(String eventId, Activity context, FragmentManager fragmentManager, boolean tabletMode) {
        if(!tabletMode){
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(DetailActivity.ARG_EVENT_ID, eventId);
            context.startActivity(intent);
        } else {
            DetailFragment frag = DetailFragment.newInstance(eventId, true);
            frag.show(fragmentManager, "dialog");
        }
    }
}
