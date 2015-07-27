package com.android.lolvoice.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public abstract class SpeechRecognitionFragment extends BaseFragment {

    private static final String TAG = "SpeechFragment";

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mRegonizerIntent;

    @Override
    public void onDestroyView() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
        }
        super.onDestroyView();
    }

    @Override
    protected void init() {
        super.init();
        boolean b = SpeechRecognizer.isRecognitionAvailable(getActivity());
        //Log.e(TAG, "Available: " + b);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        mSpeechRecognizer.setRecognitionListener(new SpeechListener());
        mRegonizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRegonizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mRegonizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getActivity().getPackageName());
    }

    @Override
    public void onDestroy() {
        if (mSpeechRecognizer != null) mSpeechRecognizer.destroy();
        super.onDestroy();
    }

    public void startListening() {
        mSpeechRecognizer.startListening(mRegonizerIntent);
    }

    private class SpeechListener implements RecognitionListener {

        @Override
        public void onResults(Bundle results) {
            //Log.e(TAG, "Results");
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            StringBuilder text = new StringBuilder();
            for (String result : matches)
                text.append(result.toLowerCase() + " ");
            //Log.e(TAG, text.toString());
        }

        @Override
        public void onEndOfSpeech() {
            //Log.e(TAG, "End of Speech");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            //Log.e(TAG, "Ready for speech");
        }

        @Override
        public void onBeginningOfSpeech() {
            //Log.e(TAG, "Beggining");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            //Log.e(TAG, "Partial results");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            //Log.e(TAG, "Event");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            //Log.e(TAG, "Buffer Received");
        }

        @Override
        public void onError(int error) {
            //Log.e(TAG, "Error: " + SpeechRecognitionUtils.getErrorText(error));
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // Log.e(TAG, "RMS: " + rmsdB);
        }
    }
}
