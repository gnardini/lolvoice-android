package com.android.lolvoice.utils;

import android.speech.SpeechRecognizer;

import com.android.lolvoice.models.Role;
import com.android.lolvoice.models.Spell;

import java.util.List;

public class SpeechRecognitionUtils {

    public static Role getRole(List<String> results) {
        for (String result : results) {
            //Log.e("Result: ", result);
            for (Role role : Role.values()) {
                for (String name : role.getNames())
                    if (result.contains(name)) return role;
            }
        }
        return null;
    }

    public static Spell getSpell(List<String> results) {
        for (String result : results) {
            //Log.e("Result: ", result);
            for (Spell spell : Spell.values()) {
                for (String name : spell.getNames())
                    if (result.contains(name)) return spell;
            }
        }
        return null;
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
