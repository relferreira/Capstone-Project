package com.relferreira.gitnotify.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by relferreira on 10/29/16.
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private List<GitNotificationsFragment> listFragments = new ArrayList<>();
    private List<String> listFragmentsTitle =  new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(GitNotificationsFragment frag, String title){
        this.listFragments.add(frag);
        this.listFragmentsTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return listFragmentsTitle.get(position);
    }

    public void reset() {
        listFragments.clear();
        listFragmentsTitle.clear();
        notifyDataSetChanged();
    }
}