package com.android.lolvoice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import java.util.List;

import com.android.lolvoice.R;
import com.android.lolvoice.fragments.CurrentGameFragment;
import com.android.lolvoice.utils.SpeechRecognitionUtils;

public class CurrentGameActivity extends TTSActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(this, SpeechRecognitionService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, SpeechRecognitionService.class));
    }
}
