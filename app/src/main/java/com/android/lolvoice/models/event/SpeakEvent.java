package com.android.lolvoice.models.event;

public class SpeakEvent {

    private String mText;

    public SpeakEvent(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }
}
