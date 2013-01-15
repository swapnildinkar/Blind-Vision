package com.example.demo;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class BackgroundService extends Service {

private ShakeListener mShaker;
	
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {
	   //final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

	    mShaker = new ShakeListener(this);
	    mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
	      public void onShake()
	      {
	        Toast.makeText(BackgroundService.this, "Shake Detected!", Toast.LENGTH_SHORT).show();
	        startActivity(new Intent(BackgroundService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
	      }
	    });
   }

   @Override
   public void onDestroy() {
      //code to execute when the service is shutting down
   }

   @Override
   public void onStart(Intent intent, int startid) {
      //code to execute when the service is starting up
   }
}
