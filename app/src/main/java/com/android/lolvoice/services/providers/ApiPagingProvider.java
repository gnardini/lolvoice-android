package com.android.lolvoice.services.providers;

import java.util.ArrayList;
import java.util.List;

import com.android.lolvoice.services.RequestCallback;

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

    public void saveOfflineData(T apiResponse) {
    }

    public List getOfflineData() {
        return new ArrayList();
    }

    private void storeCallData(int items, int page, RequestCallback<T> callback) {
        this.items = items;
        this.page = page;
        this.callback = callback;
    }
}