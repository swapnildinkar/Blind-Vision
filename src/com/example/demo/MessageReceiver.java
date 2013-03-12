package com.example.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {

	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	String from, msg;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String str = "";
		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				str += "SMS from " + msgs[i].getOriginatingAddress();
				from = msgs[i].getOriginatingAddress();
				str += " :";
				str += msgs[i].getMessageBody().toString();
				msg = msgs[i].getMessageBody().toString();
				str += "\n";
			}
			Log.v("BV", str);
			Intent messageActivity = new Intent(context, MessageActivity.class);
			messageActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			messageActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			messageActivity.putExtra("name", getCName(context, from));
			messageActivity.putExtra("message", msg);
			context.startActivity(messageActivity);
		}
	}

	public String getCName(Context context, String number) {
		// / number is the phone number
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID,
				PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,
				mPhoneNumberProjection, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return cur
						.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} finally {
			if (cur != null)
				cur.close();
		}
		return number;
	}

}
