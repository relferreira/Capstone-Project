package com.relferreira.gitnotify.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relferreira.gitnotify.R;

/**
 * Created by relferreira on 10/29/16.
 */
public class GitNotificationsFragment extends Fragment{


    public static GitNotificationsFragment newInstance(){
        GitNotificationsFragment frag = new GitNotificationsFragment();

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_git_notifications, container, false);
    }
}
