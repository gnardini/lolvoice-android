package com.android.lolvoice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.android.lolvoice.utils.SpeechRecognitionUtils;

import java.util.ArrayList;

public class SpeechRecognitionService extends Service implements RecognitionListener {

    private static final String TAG = "SpeechService";

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mRegonizerIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Created");
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        mRegonizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRegonizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mRegonizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        mRegonizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        startListening();
    }


    @Override
    public void onDestroy() {
        if (mSpeechRecognizer != null) mSpeechRecognizer.destroy();
        super.onDestroy();
    }

    public void startListening() {
        mSpeechRecognizer.startListening(mRegonizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.e(TAG, "Results");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder text = new StringBuilder();
        for (String result : matches)
            text.append(result.toLowerCase() + " ");
        Log.e(TAG, text.toString());
    }

    @Override
    public void onEndOfSpeech() {
        Log.e(TAG, "End of Speech");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e(TAG, "Ready for speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e(TAG, "Beggining");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.e(TAG, "Partial results");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e(TAG, "Event");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(TAG, "Buffer Received");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "Error: " + SpeechRecognitionUtils.getErrorText(error));
    }

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
