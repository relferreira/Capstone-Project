package com.relferreira.gitnotify.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.relferreira.gitnotify.ui.detail.DetailActivity;
import com.relferreira.gitnotify.ui.detail.DetailFragment;
import com.relferreira.gitnotify.ui.login.LoginActivity;
import com.relferreira.gitnotify.ui.main.MainActivity;

/**
 * Created by relferreira on 1/28/17.
 */
public class Navigator {

    private static final String DIALOG_TAG = "dialog";

    public void goToLogin(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public void goToMain(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    public void gotToDetails(String eventId, String eventType, Activity context,
                             FragmentManager fragmentManager, boolean tabletMode, View sharedElement, String transitionName) {
        if(!tabletMode){
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(DetailActivity.ARG_EVENT_ID, eventId);
            intent.putExtra(DetailActivity.ARG_EVENT_TYPE, eventType);
            if(sharedElement != null) {
                ActivityOptionsCompat activityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(context, new Pair<View, String>(sharedElement, transitionName));
                ActivityCompat.startActivity(context, intent, activityOptions.toBundle());
            } else {
                context.startActivity(intent);
            }
        } else {
            DetailFragment frag = DetailFragment.newInstance(eventId, eventType, true);
            frag.show(fragmentManager, DIALOG_TAG);
        }
    }
}
