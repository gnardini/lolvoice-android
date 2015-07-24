package com.android.lolvoice.service.callback;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class RequestCallback<T> implements Callback<T> {

    private static final String authError =
            "java.io.IOException: No authentication challenges found";

    private Context mContext;

    public RequestCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        if (retrofitError == null || mContext == null) return;
        if (authError(retrofitError)) {
//            mContext.startActivity(new Intent(mContext, LandingActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    protected boolean authError(RetrofitError retrofitError) {
        if (retrofitError == null) return false;
        if (messageIsAuthError(retrofitError) || is401(retrofitError.getResponse())) return true;
        return false;
    }

    private boolean is401(Response response) {
        return response != null && response.getStatus() == 401;
    }

    private boolean messageIsAuthError(RetrofitError retrofitError) {
        return retrofitError.getMessage() != null &&
                (retrofitError.getMessage().contains(authError) ||
                        authError.contains(retrofitError.getMessage()));
    }
}
