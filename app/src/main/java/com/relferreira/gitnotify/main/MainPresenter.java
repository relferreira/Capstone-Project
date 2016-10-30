package com.relferreira.gitnotify.main;

import com.relferreira.gitnotify.base.BasePresenter;

/**
 * Created by relferreira on 10/29/16.
 */
public class MainPresenter extends BasePresenter<MainView> {

    void loadToastMsg(){
        String msg = "teste";

        if(isViewAttached())
            getView().showToast(msg);
    }
}
