package com.relferreira.gitnotify.login;

import android.util.Log;

import com.relferreira.gitnotify.SchedulerProvider;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.base.BasePresenter;
import com.relferreira.gitnotify.model.User;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by relferreira on 1/9/17.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private final GithubService githubService;
    private final SchedulerProvider schedulerProvider;

    @Inject
    public LoginPresenter(SchedulerProvider schedulerProvider, GithubService githubService){
        this.githubService = githubService;
        this.schedulerProvider = schedulerProvider;
    }

    public void loginRequest(String username, String password) {
        if(username == null || username.isEmpty()) {
            getView().showError("Username should not be empty");
            return;
        }

        if(password == null || password.isEmpty()) {
            getView().showError("Password should not be empty");
            return;
        }

        this.githubService.getUser("relferreira")
                .compose(schedulerProvider.applySchedulers())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showError("Invalid login");
                    }

                    @Override
                    public void onNext(User user) {

                    }
                });

    }

}
