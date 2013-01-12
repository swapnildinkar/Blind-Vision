package com.example.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, BackgroundService.class);
		context.startService(serviceIntent);
		Toast.makeText(context, "Boot completed!", Toast.LENGTH_SHORT).show();
	}

}
