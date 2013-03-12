package com.example.demo;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class MessageActivity extends Activity implements TextToSpeech.OnInitListener {

	TextToSpeech tts;
	String name, message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//ttsi = new TTSInterface(this);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
		name = getIntent().getStringExtra("name");
		message = getIntent().getStringExtra("message");
		tts = new TextToSpeech(this, this);
		System.out.println("MA: " + name + ":" + message);

	}

	@Override
	public void onDestroy() {
		tts.shutdown();
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(new Locale("en","US"));
            //tts.setPitch(new Float(0.6));
            //tts.setSpeechRate(new Float(0.8));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            	//Proceed
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
		tts.speak("Message from " + name + ", " + message, TextToSpeech.QUEUE_FLUSH, null);
	}

}
