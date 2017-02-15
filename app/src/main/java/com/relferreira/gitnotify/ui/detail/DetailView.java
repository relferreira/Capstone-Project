package com.relferreira.gitnotify.ui.detail;

import com.relferreira.gitnotify.ui.base.BaseView;

import java.util.List;

/**
 * Created by relferreira on 2/5/17.
 */

public interface DetailView extends BaseView{

    void setActorImage(String image);

    void setTitle(String title);

    void setAdapterData(List items);

    void showError();

    void showPageLoading(boolean status);
}
