package com.android.lolvoice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import com.android.lolvoice.R;
import com.android.lolvoice.fragments.CurrentGameFragment;
import com.android.lolvoice.utils.TextToSpeechUtils;
import com.android.lolvoice.models.event.SpeakEvent;
import com.android.lolvoice.utils.SpeechRecognitionUtils;

import java.util.List;

import de.greenrobot.event.EventBus;

public class CurrentGameActivity extends BaseActivity {

    public static final int REQUEST_ROLE = 10;
    public static final int REQUEST_SPELL = 11;

    private CurrentGameFragment mCurrentGameFragment;

    @Override
    protected int layout() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        super.init();
        mCurrentGameFragment = CurrentGameFragment.newInstance();
        replaceFragment(R.id.home_activity_container, mCurrentGameFragment);
        TextToSpeechUtils.onCreate(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (requestCode == REQUEST_ROLE) {
                mCurrentGameFragment.onRoleSelected(SpeechRecognitionUtils.getRole(results));
            } else {
                mCurrentGameFragment.onSpellSelected(SpeechRecognitionUtils.getSpell(results));
            }
        }
    }

    public void onEvent(SpeakEvent event) {
        TextToSpeechUtils.speak(event.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(this, SpeechRecognitionService.class));
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        //stopService(new Intent(this, SpeechRecognitionService.class));
        TextToSpeechUtils.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
