package com.android.lolvoice.listeners;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    public abstract void onScrollMore(int currentPage);

    public EndlessScrollListener() {
        this(null);
    }

    public EndlessScrollListener(Integer visibleThreshold) {
        if (visibleThreshold != null)
            this.visibleThreshold = visibleThreshold;
        reset();
    }

    public void reset() {
        previousTotal = 0;
        currentPage = 0;
        loading = true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading &&
                (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onScrollMore(currentPage);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}