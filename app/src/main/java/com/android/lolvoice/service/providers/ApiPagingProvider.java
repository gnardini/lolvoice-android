package com.android.lolvoice.service.providers;

import com.android.lolvoice.service.callback.RequestCallback;

public abstract class ApiPagingProvider<T> {

    protected int items;
    protected int page;
    protected RequestCallback<T> callback;

    protected abstract void doCall();

    public final void retry() {
        doCall();
    }

    public final void provide(int items, int page, RequestCallback<T> callback) {
        storeCallData(items, page, callback);
        doCall();
    }

    private void storeCallData(int items, int page, RequestCallback<T> callback) {
        this.items = items;
        this.page = page;
        this.callback = callback;
    }
}