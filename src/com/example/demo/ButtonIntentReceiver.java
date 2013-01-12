package com.example.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

public class ButtonIntentReceiver extends BroadcastReceiver {
	MainActivity ma;
public ButtonIntentReceiver() {
	
    super();
    
}

@Override
public void onReceive(Context context, Intent intent) {
    String intentAction = intent.getAction();
    if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        return;
    }
    KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
    if (event == null) {
        return;
    }
    int action = event.getAction();
    if (action == KeyEvent.ACTION_DOWN) {
    // do something
    Toast.makeText(context, "BUTTON PRESSED!", Toast.LENGTH_SHORT).show(); 
    //ma.voiceSearch();
    Intent i = new Intent(context, MainActivity.class);
    
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(i);
    //((Class MainActivity) context).voiceSearch();
    //MainActivity.voiceSearch();
    }
    abortBroadcast();
}
}

