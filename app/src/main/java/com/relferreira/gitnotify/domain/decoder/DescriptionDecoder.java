package com.relferreira.gitnotify.domain.decoder;

import android.content.Context;

import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.util.SchedulerProvider;

/**
 * Created by relferreira on 2/5/17.
 */

public interface DescriptionDecoder {
    String getTitle();
    String getSubtitle();
    String getDetailTitle();
    void loadData(Context context, GithubInteractor interactor, Event event, SchedulerProvider schedulerProvider, DecoderListener listener);
}
