package com.relferreira.gitnotify.ui.detail;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by relferreira on 2/14/17.
 * Base on https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */

public abstract class InfinityScrollListener extends RecyclerView.OnScrollListener{

    private static final int VISIBLE_THRESHOLD = 5;
    private static final int START_PAGE = 1;

    private LinearLayoutManager layoutManager;
    private int currentPage = START_PAGE;
    private int previousTotalItemCount = 0;
    private boolean loading;

    public InfinityScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int totalItemCount = layoutManager.getItemCount();
        int lastItemPosition = layoutManager.findLastVisibleItemPosition();

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = START_PAGE;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastItemPosition + VISIBLE_THRESHOLD) > totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, recyclerView);
            loading = true;
        }
    }

    public void reset() {
        currentPage = START_PAGE;
        previousTotalItemCount = 0;
        loading = false;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
