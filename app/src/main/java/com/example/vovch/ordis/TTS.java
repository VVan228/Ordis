package com.example.vovch.ordis;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class TTS implements TextToSpeech.OnInitListener{
    static TextToSpeech textToSpeech;
    boolean ttsEnabled = false;
    String text;
    public void run(String text, Context context){
        this.text = text;
        textToSpeech = new TextToSpeech(context, this);

}

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (textToSpeech.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                    == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));
            } else {
                textToSpeech.setLanguage(Locale.US);
            }
            textToSpeech.setPitch(1.3f);
            textToSpeech.setSpeechRate(0.7f);
            ttsEnabled = true;
        } else if (status == TextToSpeech.ERROR) {
            ttsEnabled = false;
        }
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
}
