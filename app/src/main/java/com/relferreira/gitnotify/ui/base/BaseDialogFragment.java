package com.relferreira.gitnotify.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relferreira.gitnotify.GitNotifyApplication;
import com.relferreira.gitnotify.injector.ApplicationComponent;

/**
 * Created by relferreira on 2/6/17.
 */

public abstract class BaseDialogFragment extends DialogFragment implements BaseView{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        injectFragment(getApplicationComponent());
        return view;
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((GitNotifyApplication) getActivity().getApplication()).getApplicationComponent();
    }

    public abstract void injectFragment(ApplicationComponent component);
}