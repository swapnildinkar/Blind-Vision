package com.blindvision.main;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public ButtonIntentReceiver r;
	public TakePic tp;
	public static final int code = 111;
	public EditText etQuery;
	public Button btnSearch, btnVoiceSearch;
	public int mode;
	public boolean serviceFlag;
	public String query;
	public static String result;
	public String domain = "http://mess.byethost31.com/bv/vserver.php?query=";
	public ServerInterface serverInterface;
	public Node response;
	public TextView txtStatus, txtUnderstood, txtAnswered, txtResult;
	public static TTSInterface tts;
	public AudioManager am;
	public PowerManager.WakeLock wl;
	static JSONObject jObj = null;
	Button buttonClick;
	String action, contact, name, message;
	public final static String FIRST_RUN = "firstRun";
	public final static String IS_REGISTERED = "isRegistered";
	public final static String BLINDVISION_PREFS = "blindvisionprefs";
	public boolean isRegistered;

	static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.STARRED,
			ContactsContract.Contacts.TIMES_CONTACTED,
			ContactsContract.Contacts.CONTACT_PRESENCE,
			ContactsContract.Contacts.PHOTO_ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.HAS_PHONE_NUMBER, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serverInterface = new ServerInterface();
		tts = new TTSInterface(this);
		mode = 1;
		voiceSearch();
		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
		r = new ButtonIntentReceiver();
		filter.setPriority(10000);
		registerReceiver(r, filter);

		isRegistered = this.getSharedPreferences(BLINDVISION_PREFS, 0)
				.getBoolean(IS_REGISTERED, false);
		if (!isRegistered) {
			new RegisterTask().execute();
		}
		if (!BackgroundService.instance) {
			startService(new Intent(this, BackgroundService.class));
		}
		if (!LocationService.linstance && isRegistered) {
			startService(new Intent(this, LocationService.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void choice(String c) {
		if (c.equals("navigate")) {
		} else if (c.equals("read")) {
			startActivity(new Intent(this, TakePic.class));
			finish();
		} else if (c.equals("alarm")) {

		} else {
			call(c);
		}
	}

	public void call(String query) {
		this.query = Uri.encode(query);
		LoadTask loadTask = new LoadTask();
		loadTask.execute((String[]) null);

	}

	public void voiceSearch() {
		// tts.preSpeak("Hello, What do you want to do ?");
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak!");
		startActivityForResult(i, code);
	}

	private class LoadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			response = serverInterface.getJSONFromUrl(domain + query);
			if (response == null) {
				Toast.makeText(MainActivity.this, "null", 2000).show();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(MainActivity.this, result, 2000).show();
			process(result);
		}
	}

	private class RegisterTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String reply = serverInterface.register(MainActivity.this);
			return reply;
		}

		@Override
		protected void onPostExecute(String reply) {
			String bid = "";
			String bkey = "";
			JSONObject jObj;
			try {
				jObj = new JSONObject(reply);
				result = jObj.getString("result");
				bid = jObj.getString("bid");
				bkey = jObj.getString("bkey");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (result.equals("SUCCESS")) {
				Editor passwdfile = getSharedPreferences(BLINDVISION_PREFS, 0)
						.edit();
				passwdfile.putBoolean(IS_REGISTERED, true);
				passwdfile.putString("bid", bid);
				passwdfile.putString("bkey", bkey);
				passwdfile.commit();
				Toast.makeText(MainActivity.this, "Registration Successful!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void process(String JSON) {

		try {
			jObj = new JSONObject(JSON);
			action = jObj.getString("action");

			if (action.equals("CALL")) {
				Toast.makeText(this, "CALL", Toast.LENGTH_LONG).show();
				contact = jObj.getString("contact");
				result = "call " + contact;

				String select = "(" + ContactsContract.Contacts.DISPLAY_NAME
						+ " == \"" + contact + "\" )";
				Cursor c = this.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI,
						CONTACTS_SUMMARY_PROJECTION,
						select,
						null,
						ContactsContract.Contacts.DISPLAY_NAME
								+ " COLLATE LOCALIZED ASC");
				this.startManagingCursor(c);

				if (c.moveToNext()) {
					String id = c.getString(0);
					ArrayList<String> phones = new ArrayList<String>();

					Cursor pCur = this.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						phones.add(pCur.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
						Log.i("",
								contact
										+ " has the following phone number "
										+ pCur.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					}
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + phones.get(0)));
					startActivity(callIntent);
					pCur.close();
				}
			} else if (action.equals("MESSAGE")) {
				//Toast.makeText(this, "MESSAGE", Toast.LENGTH_LONG).show();
				contact = jObj.getString("contact");
				message = jObj.getString("message");
				result = "message " + contact + message;

				if(message.equalsIgnoreCase("information")) {
					SharedPreferences file = this.getSharedPreferences(
							"blindvisionprefs", 0);
					String bid = file.getString("bid", "");
					String bkey = file.getString("bkey", "");
					message = "ID: " + bid + "\nKEY:" + bkey;
				}

				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				String select = "(" + ContactsContract.Contacts.DISPLAY_NAME
						+ " == \"" + contact + "\" )";
				Cursor c = this.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI,
						CONTACTS_SUMMARY_PROJECTION,
						select,
						null,
						ContactsContract.Contacts.DISPLAY_NAME
								+ " COLLATE LOCALIZED ASC");
				this.startManagingCursor(c);

				if (c.moveToNext()) {
					String id = c.getString(0);
					ArrayList<String> phones = new ArrayList<String>();

					Cursor pCur = this.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						phones.add(pCur.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
						Log.i("",
								contact
										+ " has the following phone number "
										+ pCur.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					}
					PendingIntent pi = PendingIntent.getActivity(this, 0,
							new Intent(this, MainActivity.class), 0);
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(phones.get(0), null, message, pi, null);
					pCur.close();
				}
			} else if (action.equals("KNOWLEDGE")) {
				result = jObj.getString("knowledge");
			}
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.getMessage());
			result = "Error while processing";
		}

		tts.preSpeak(result);
		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		result = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (tts.tts != null)
			tts.tts.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (tts == null || tts.tts == null)
			tts = new TTSInterface(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == code && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String result = results.get(0);
			for (int i = 0; i < results.size(); i++) {
				Log.v("Blind Vision", results.get(i));
			}
			if (result.equalsIgnoreCase("read")
					|| result.equalsIgnoreCase("reid")
					|| result.equalsIgnoreCase("read it")
					|| result.equalsIgnoreCase("reed")) {
				startActivity(new Intent(this,
						com.blindvision.ocr.CaptureActivity.class));
			} else if (result.equalsIgnoreCase("navigate")) {
				startActivity(new Intent(this,
						com.blindvision.main.IOIOUltrasonicSensorActivity.class));
			} else {
				call(result);
			}
		} else {
			// yet to be tested.
			// voiceSearch();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onDestroy() {
		if (tts.tts != null) {
			tts.tts.stop();
			tts.tts.shutdown();
			unregisterReceiver(r);
		}
		super.onDestroy();
	}
}