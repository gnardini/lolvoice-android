package com.android.lolvoice.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.lolvoice.listeners.DataClickableListener;
import com.android.lolvoice.listeners.EndlessScrollListener;
import com.android.lolvoice.listeners.OnDataClickedListener;
import com.android.lolvoice.service.callback.RequestCallback;
import com.android.lolvoice.service.providers.ApiPagingProvider;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class ScrollListFragment<T> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener{

    public interface OnDataReceivedTransformer<T> {
        public List<T> transform(List<T> list);
    }

    public static final int ITEMS_PER_PAGE = 20;
    public static final int DEFAULT_SCROLL_AMOUNT = 10;

    protected enum Status { LOADING, LOADED, ERROR }

    private BaseAdapter mAdapter;
    private AbsListView.OnScrollListener mOnScrollListener;

    private int mMargin;
    private boolean mErroDisabled;
    private ConnectivityManager mConnectivityManager;

    protected int mRetryAmount;
    protected Deque<Integer> mLoadQueue = new LinkedList<Integer>();
    protected Status mStatus;
    protected List<T> mList = new ArrayList<>();
    protected ApiPagingProvider mProvider;
    protected OnDataClickedListener<T> mOnDataClickedListener;
    protected OnDataReceivedTransformer<T> mOnDataReceivedTransformer;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mLoadingView;
    protected View mErrorView;
    protected View mHeaderView;
    protected View mNoResultsView;
    protected View mOfflineModeView;
    protected ImageView mOfflineRefresh;
    protected ViewGroup mReloadViewGroup;
    protected EndlessScrollListener mEndlessScrollListener;
    protected AbsListView mCollectionView;

    protected abstract ApiPagingProvider loadProvider();
    protected abstract BaseAdapter loadAdapter();

    public void reload() {
        if (mList == null || mAdapter == null) return;
        mList.clear();
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
        clearQueue();
        loadMore(0);
        if (mEndlessScrollListener != null) mEndlessScrollListener.reset();
    }

    @Override
    public void onRefresh() {
        reload();
    }

    protected int getEndlessScrollAmount() {
        return DEFAULT_SCROLL_AMOUNT;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public void setOnDataClickedListener(OnDataClickedListener<T> listener) {
        mOnDataClickedListener = listener;
        if (mAdapter != null && mAdapter instanceof DataClickableListener) {
            ((DataClickableListener) mAdapter).setOnDataClickedListener(mOnDataClickedListener);
        }
    }

    public void setNoResultMargin(int margin) {
        if (mNoResultsView == null) {
            mMargin = margin;
            return;
        }
        setMargin();
    }

    public void setNoResultsView(View mNoResultsView) {
        this.mNoResultsView = mNoResultsView;
        if (mMargin == 0) return;
        setMargin();
    }

    protected void init() {
        if (mList == null) mList = new ArrayList<>();
        checkListIsEmpty();
        mProvider = loadProvider();
        mAdapter = loadAdapter();
        mConnectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        initScrollListFragment(this);
    }

    protected void setListeners() {
        if (mOfflineModeView == null) return;
        mOfflineModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMore(0);
            }
        });
    }

    protected void initScrollListFragment(SwipeRefreshLayout.OnRefreshListener target) {
        mSwipeRefreshLayout.setOnRefreshListener(target);

        mEndlessScrollListener = new EndlessScrollListener(getEndlessScrollAmount()) {
            @Override
            public void onScrollMore(int currentPage) {
                loadMore(currentPage);
            }
        };
        mReloadViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });
        initializeListView();
        if (mList != null && mList.isEmpty()) loadMore(0);
        setAdapter();
    }

    protected void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    protected void setStatusLoading() {
        if (mStatus == Status.LOADING) return;
        mStatus = Status.LOADING;
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mNoResultsView.setVisibility(View.GONE);
        addFooter();
    }

    protected void setStatusLoaded() {
        if (mStatus == Status.LOADING) removeFooter();
        mStatus = Status.LOADED;
        mSwipeRefreshLayout.setRefreshing(false);
        mErrorView.setVisibility(View.GONE);
        checkListIsEmpty();
    }

    protected void checkListIsEmpty() {
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        if (mList.isEmpty()) {
            mReloadViewGroup.setVisibility(View.VISIBLE);
            mNoResultsView.setVisibility(View.VISIBLE);
        } else {
            mReloadViewGroup.setVisibility(View.GONE);
            mNoResultsView.setVisibility(View.GONE);
        }
    }

    protected void setStatusError() {
        if (mStatus == Status.LOADING) removeFooter();
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mStatus = Status.ERROR;
        mNoResultsView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.invalidate();
        mNoResultsView.invalidate();
        mLoadingView.invalidate();
        mSwipeRefreshLayout.invalidate();
        mReloadViewGroup.invalidate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mStatus == Status.LOADING) removeFooter();
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

                mStatus = Status.ERROR;
                mNoResultsView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.invalidate();
                mNoResultsView.invalidate();
                mLoadingView.invalidate();
                mSwipeRefreshLayout.invalidate();
                mReloadViewGroup.invalidate();
                mReloadViewGroup.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    protected void loadMore(final int currentPage) {
        if (mProvider == null)  return;
        if (mStatus == Status.LOADING) {
            if (currentPage > 0) mLoadQueue.offer(currentPage);
            return;
        }
        setStatusLoading();
        setAdapter();
        if (isNetworkAvailable()) {
             mProvider.provide(ITEMS_PER_PAGE,
                    currentPage + 1,
                    new RequestCallback<List<T>>(getActivity()) {
                        @Override
                        public void success(List<T> list, Response response) {
                            mRetryAmount = 0;
                            if (mOnDataReceivedTransformer != null) {
                                list = mOnDataReceivedTransformer.transform(list);
                            }
                            if (currentPage == 0) {
                                mList.clear();
                                if (mOfflineModeView != null) mOfflineModeView.setVisibility(View.GONE);
                            }
                            mList.addAll(list);
                            mAdapter.notifyDataSetChanged();
                            setStatusLoaded();
                            if (!mLoadQueue.isEmpty()) loadMore(mLoadQueue.poll());
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            super.failure(retrofitError);
                            if (mErroDisabled) {
                                setStatusLoaded();
                            } else {
                                setStatusError();
                            }
                        }
                    }
            );
        }
    }

    protected void setAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
        if (mOnDataClickedListener != null && mAdapter instanceof DataClickableListener) {
            ((DataClickableListener) mAdapter).setOnDataClickedListener(mOnDataClickedListener);
        }
    }

    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    protected void setErrorDisabled() {
        mErroDisabled = true;
    }

    protected void clearQueue() {
        mLoadQueue.clear();
    }

    protected void setAdapter() {
        if (mCollectionView.getAdapter() == null) {
            mCollectionView.setAdapter(mAdapter);
        }
    }

    private void setMargin() {
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mNoResultsView.getLayoutParams();
        p.setMargins(0, mMargin, 0, 0);
    }

    private void initializeListView() {
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                mEndlessScrollListener.onScrollStateChanged(absListView, i);
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(absListView, i);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                mEndlessScrollListener.onScroll(absListView, i, i2, i3);
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(absListView, i, i2, i3);
                }
            }
        };
        mCollectionView.setOnScrollListener(onScrollListener);
        if (mCollectionView instanceof ListView) {
            ((ListView) mCollectionView).addFooterView(mLoadingView);
            if (mHeaderView != null) ((ListView) mCollectionView).addHeaderView(mHeaderView);
        }
    }

    private void addFooter() {
        if (!(mCollectionView instanceof ListView)) return;
        ((ListView) mCollectionView).addFooterView(mLoadingView);
    }

    private void removeFooter() {
        if (!(mCollectionView instanceof ListView)) return;
        ((ListView) mCollectionView).removeFooterView(mLoadingView);
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

