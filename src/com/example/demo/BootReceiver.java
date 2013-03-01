package com.example.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, BackgroundService.class));
		context.startService(new Intent(context, LocationService.class));
		Toast.makeText(context, "Blind Vision Service started!", Toast.LENGTH_SHORT).show();
	}
}
