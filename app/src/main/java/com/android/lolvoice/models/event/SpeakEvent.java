package com.android.lolvoice.models.event;

import android.support.annotation.StringRes;

import com.android.lolvoice.LoLVoiceApplication;

public class SpeakEvent {

    private String mText;

    public SpeakEvent(String text) {
        mText = text;
    }

    public SpeakEvent(@StringRes int resId) {
        mText = LoLVoiceApplication.getAppContext().getString(resId);
    }

    public String getText() {
        return mText;
    }
}
