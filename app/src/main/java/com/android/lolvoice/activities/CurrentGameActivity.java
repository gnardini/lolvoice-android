package com.android.lolvoice.activities;

import android.content.Intent;
import android.speech.RecognizerIntent;

import java.util.List;

import com.android.lolvoice.R;
import com.android.lolvoice.fragments.CurrentGameFragment;

public class CurrentGameActivity extends BaseActivity {

    public static final int SPEECH_REQUEST_CODE = 10;

    private CurrentGameFragment mCurrentGameFragment;

    @Override
    protected int layout() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        mCurrentGameFragment = CurrentGameFragment.newInstance();
        replaceFragment(R.id.home_activity_container, mCurrentGameFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mCurrentGameFragment.onSpeachResults(results);
        }
    }
}
