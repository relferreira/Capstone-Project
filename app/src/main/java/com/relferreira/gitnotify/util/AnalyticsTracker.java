package com.relferreira.gitnotify.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by relferreira on 2/16/17.
 */

public class AnalyticsTracker {

    private Tracker tracker;

    public AnalyticsTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public void sendScreenTrack(String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEventSelectionTrack(String eventType) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(eventType)
                .build());
    }
}
