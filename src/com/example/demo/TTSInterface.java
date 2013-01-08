package com.example.demo;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import com.example.BlindVision.R;

public class TTSInterface implements TextToSpeech.OnInitListener {
	
	TextToSpeech tts;
	
	public TTSInterface(Context context) {
		tts = new TextToSpeech(context, this);
		tts.setPitch(2.0f);
		tts.setSpeechRate(2.0f);
	}
	
	public void speak(Response response) {
		if(response.isAnswered() && response.isUnderstood)
			tts.speak(response.getResult(), TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(new Locale("en","US"));
            tts.setPitch(new Float(0.6));
            tts.setSpeechRate(new Float(0.8));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            	//Proceed
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
	}
	
	public void stop() {
		tts.stop();
		tts.shutdown();
	}

}
