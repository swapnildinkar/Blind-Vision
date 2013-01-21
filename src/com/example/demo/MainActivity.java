package com.example.demo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * mode
 * 1-find out what the user wants
 * 2-search
 * 3-ocr
 * 4-ioio
 */
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
	// public String domain =
	// "https://api.trueknowledge.com/direct_answer?object_metadata=image128,wikipedia,official%20&question=";
	public String domain = "http://blindvision.net84.net/blindvision/maluuba.php?query=";
	public String username = "&api_account_id=api_blindvision";
	public String password = "&api_password=mxftf8bi2bopl5is";
	public ServerInterface serverInterface;
	public XMLParser myParser;
	public Document doc;
	public Node response;
	public TextView txtStatus, txtUnderstood, txtAnswered, txtResult;
	public static TTSInterface tts;
	public AudioManager am;
	public PowerManager.WakeLock wl;
	static JSONObject jObj = null;
	Button buttonClick;
	String action, contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// etQuery = (EditText) findViewById(R.id.etQuery);
		// txtStatus = (TextView) findViewById(R.id.txtStatus);
		// txtUnderstood= (TextView) findViewById(R.id.txtUnderstood);
		// txtAnswered = (TextView) findViewById(R.id.txtAnswered);
		// txtResult = (TextView) findViewById(R.id.txtResult);
		// btnSearch = (Button) findViewById(R.id.btnSearch);
		// btnVoiceSearch = (Button) findViewById(R.id.btnVoiceSearch);
		// btnSearch.setOnClickListener(this);
		// btnVoiceSearch.setOnClickListener(this);
		serverInterface = new ServerInterface();
		myParser = new XMLParser();
		tts = new TTSInterface(this);
		// tts.speak(new Response(true,true,"what do you want to do ?",".."));
		mode = 1;
		// String TAG = MainActivity.class.getSimpleName();

		// Log.d(TAG, "TAG text");
		// PowerManager pm = (PowerManager) this
		// .getSystemService(Context.POWER_SERVICE);
		// wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		// wl.acquire();
		// startService(new Intent(this, BackgroundService.class));
		voiceSearch();
		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
		r = new ButtonIntentReceiver();
		filter.setPriority(10000);
		registerReceiver(r, filter);
		if (!BackgroundService.instance) {
			startService(new Intent(this, BackgroundService.class));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public String stt() {
		voiceSearch();
		return ("");

	}

	public void choice(String c) {
		if (c.equals("navigate")) {
			// mode=2;
			// voiceSearch();
		} else if (c.equals("read")) {
			startActivity(new Intent(this, TakePic.class));
			finish();
		}
		// else if(c.equals(null))
		// voiceSearch();
		else if (c.equals("alarm")) {

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

	public void process(String JSON) {

		try {
			jObj = new JSONObject(JSON);
			action = jObj.getString("action");

			if (action.equals("CALL")) {
				Toast.makeText(this, "CALL", Toast.LENGTH_LONG).show();
				result="call " + jObj.getString("contact");
			} else if (action.equals("KNOWLEDGE")) {
				result = jObj.getString("knowledge");
			}
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		//String status = "", result = "";
		boolean understood = false;
		boolean answered = false;
		/*
		 * doc = myParser.getDomElement(XML); NodeList nl =
		 * doc.getElementsByTagName("tk:response"); for (int i = 0; i <
		 * nl.getLength(); i++) { Element e = (Element) nl.item(i); status
		 * =myParser.getValue(e, "tk:status"); understood
		 * =e.getAttribute("understood").equals("true"); answered
		 * =e.getAttribute("answered").equals("true"); result =
		 * myParser.getValue(e, "tk:text_result"); }
		 * //txtStatus.setText("Status: " + status);
		 * //txtUnderstood.setText("Understood: " + understood);
		 * //txtAnswered.setText("Answered: " + answered);
		 * //txtResult.setText(result); //tts.speak(new
		 * //Response(true,true,"Hello","world")); //tts.speak(new
		 * Response((boolean)understood, (boolean)answered, result, status));
		 */


		//tts=new TTSInterface(this);
		tts.preSpeak(result);
		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		//while (tts.tts.isSpeaking()) {
			
			//tts.stop();
			// voiceSearch();
		//}
		result=null;
		//voiceSearch();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == code && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String result = results.get(0);
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

			choice(result); // call(result);
		} else {

		}

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			unregisterReceiver(r);
		}
		super.onDestroy();
		// wl.release();
	}

}
/*
 * class VoiceTyping extends InputMethodService { VoiceRecognitionTrigger
 * mVoiceRecognitionTrigger; protected void onCreate(Bundle savedInstanceState)
 * { mVoiceRecognitionTrigger = new VoiceRecognitionTrigger(this); } public void
 * startVoice() { mVoiceRecognitionTrigger.startVoiceRecognition(); }
 * 
 * }
 */