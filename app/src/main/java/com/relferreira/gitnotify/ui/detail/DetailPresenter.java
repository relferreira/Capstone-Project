package com.relferreira.gitnotify.ui.detail;

import android.content.Context;
import android.util.Log;

import com.relferreira.gitnotify.domain.EventInteractor;
import com.relferreira.gitnotify.domain.GithubInteractor;
import com.relferreira.gitnotify.domain.decoder.DecoderListener;
import com.relferreira.gitnotify.domain.decoder.DescriptionDecoder;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;
import com.relferreira.gitnotify.ui.base.BasePresenter;
import com.relferreira.gitnotify.util.SchedulerProvider;

import java.util.List;

/**
 * Created by relferreira on 2/11/17.
 */

public class DetailPresenter extends BasePresenter<DetailView> implements DecoderListener {

    private StringRepository stringRepository;
    private EventInteractor eventInteractor;
    private GithubInteractor githubInteractor;
    private SchedulerProvider schedulerProvider;
    private DescriptionDecoder decoder;


    public DetailPresenter(StringRepository stringRepository, EventInteractor eventInteractor,
                           GithubInteractor githubInteractor, SchedulerProvider schedulerProvider) {
        this.stringRepository = stringRepository;
        this.eventInteractor = eventInteractor;
        this.githubInteractor = githubInteractor;
        this.schedulerProvider = schedulerProvider;
    }

    public void getDecoder(Context context, Event event) {
        if(isViewAttached())
            getView().showLoading(true);

        decoder = eventInteractor.getDecoder(stringRepository, event, event.type());

        if(isViewAttached()) {
            DetailView view = getView();
            view.setActorImage(event.actor().avatarUrl());
            view.setTitle(decoder.getDetailTitle());
        }

        decoder.loadData(context, githubInteractor, event, schedulerProvider, this);

    }

    public void loadPage(Context context, Event event, int page) {
        decoder.loadPage(context, githubInteractor, event, schedulerProvider, this, page);
    }

    @Override
    public void successLoadingData(List items) {
        Log.i("teste", "teste");
        if(isViewAttached())
            getView().setAdapterData(items);
    }

    @Override
    public void errorLoadingData(String error) {
        Log.e("teste", "error");
        if(isViewAttached())
            getView().showError();
    }

    @Override
    public void showPageLoading(boolean status) {
        if(isViewAttached())
            getView().showPageLoading(status);
    }
}
