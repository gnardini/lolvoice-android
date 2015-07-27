package com.android.lolvoice.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.android.lolvoice.R;

import java.util.Locale;

public class TextToSpeechUtils {

    private static Activity mContext;
    private static TextToSpeech mTextToSpeech;

    public static void onCreate(Activity context) {
        mContext = context;
        init();
    }

    private static void init() {
        if (mTextToSpeech != null) return;
        mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mTextToSpeech.setLanguage(Locale.US);
                        }
                    }).start();
                } else {
                    // TODO Install languages if missing
                    Toast.makeText(mContext, R.string.no_tts_installed, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public static void speak(String text) {
        if (mTextToSpeech == null) return;
        if (!ScreenLockUtils.isScrenOn(mContext.getApplicationContext()))
            ScreenLockUtils.wakeScreen(mContext);
        mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        else mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public static void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
}
