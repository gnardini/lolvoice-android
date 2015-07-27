package com.android.lolvoice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.lolvoice.utils.TextToSpeechUtils;

public class TextToSpeechService extends Service {

    private TextToSpeechUtils mTextToSpeechHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        //new TextToSpeechHelper(this);
    }

    @Override
    public void onDestroy() {
        mTextToSpeechHelper.onDestroy();
        super.onDestroy();
    }

    public void speak(String text) {
        mTextToSpeechHelper.speak(text);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
