package com.example.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class BackgroundService extends Service {
public static boolean instance;
private ShakeListener mShaker;
	//public TTSInterface tts;
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {
	   //final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	   instance=true;
	    mShaker = new ShakeListener(this);
	   
	    mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
	      public void onShake()
	      {
	    	if(MainActivity.tts.tts.isSpeaking() && MainActivity.tts.tts!=null)
	  	    {
	        //Toast.makeText(BackgroundService.this, "Shake Detected!", Toast.LENGTH_SHORT).show();
	    	MainActivity.tts.tts.stop();
	    	
	        //MainActivity.tts.tts.speak("...", TextToSpeech.QUEUE_FLUSH, null);
	        //MainActivity.tts = new TTSInterface(MainActivity.this);
	        //MainActivity.tts.preSpeak(MainActivity.result);
	        //startActivity(new Intent(BackgroundService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
	      }
	    	else
	    		startActivity(new Intent(BackgroundService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
	      }	      
	    });
	    
	    
	    
   }

   public boolean isServiceCreated()
   {
	   return instance;
   }
   @Override
   public void onDestroy() {
	   instance=false;
      //code to execute when the service is shutting down
   }

   @Override
   public void onStart(Intent intent, int startid) {
      //code to execute when the service is starting up
   }
}
