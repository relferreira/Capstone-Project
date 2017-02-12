package com.relferreira.gitnotify.ui.pages;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by relferreira on 2/11/17.
 */

public abstract class PagesAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    public abstract void setItems(List items);
}
