package com.android.lolvoice.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import com.android.lolvoice.models.event.SpeakEvent;

import java.util.Locale;

import de.greenrobot.event.EventBus;

public abstract class TTSActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    private static final int DATA_CHECK = 5;

    private TextToSpeech mTextToSpeech;

    @Override
    protected void init() {
        super.init();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, DATA_CHECK);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTextToSpeech.setLanguage(Locale.US);
        }
    }

    public void onEvent(SpeakEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mTextToSpeech.speak(event.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
        else mTextToSpeech.speak(event.getText(), TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DATA_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTextToSpeech = new TextToSpeech(this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
