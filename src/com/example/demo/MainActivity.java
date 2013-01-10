package com.example.demo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BlindVision.R;
/*
 * mode
 * 1-find out what the user wants
 * 2-search
 * 3-ocr
 * 4-ioio
 */
public class MainActivity extends Activity {

	public TakePic tp;
	public static final int code = 111;
	public EditText etQuery;
	public Button btnSearch, btnVoiceSearch;
	public int mode;
	public String query;
	public String result;
	public String domain = "https://api.trueknowledge.com/direct_answer?object_metadata=image128,wikipedia,official%20&question=";
	public String username = "&api_account_id=api_blindvision";
	public String password = "&api_password=mxftf8bi2bopl5is";
	public ServerInterface serverInterface;
	public XMLParser myParser;
	public Document doc;
	public Node response;
	public TextView txtStatus, txtUnderstood, txtAnswered, txtResult;
	public TTSInterface tts;
	Button buttonClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//etQuery =  (EditText) findViewById(R.id.etQuery);
		//txtStatus = (TextView) findViewById(R.id.txtStatus);
		//txtUnderstood= (TextView) findViewById(R.id.txtUnderstood);
		//txtAnswered = (TextView) findViewById(R.id.txtAnswered);
		//txtResult = (TextView) findViewById(R.id.txtResult);
		//btnSearch = (Button) findViewById(R.id.btnSearch);
		//btnVoiceSearch = (Button) findViewById(R.id.btnVoiceSearch);
		//btnSearch.setOnClickListener(this);
		//btnVoiceSearch.setOnClickListener(this);
		serverInterface = new ServerInterface();
		myParser = new XMLParser();
		tts = new TTSInterface(this);
		//tts.speak(new Response(true,true,"what do you want to do ?",".."));
		mode=1;
		
		while(tts.tts.isSpeaking()){}
		voiceSearch();
		//call("obama");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public String stt()
	{
		voiceSearch();
		return("");
		
	}
	
	public void choice(String c)
	{
		if(mode==1)
		{
			if(c.equals("search"))
			{
				mode=2;
				voiceSearch();
				
			}
			else if(c.equals("read"))
			{
				startActivity(new Intent(this, TakePic.class));
				finish();
			}
		}
		else if(mode==2)
		{
			call(c);
			mode=1;
		}
		else if(mode==3)
		{
			//ocr
		}
		else if(mode==4)
		{
			//ioio
		}
		else if(mode==5)
		{
			//back
		}
	}
	
	public void call(String query) {
		this.query = Uri.encode(query);
		LoadTask loadTask = new LoadTask();
		loadTask.execute((String[])null);
		
	}
	
	private void voiceSearch() {
		//tts.preSpeak("Hello, What do you want to do ?");
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak!");
		startActivityForResult(i, code);
	}

	private class LoadTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... urls) {
	      String response = "";
	      response = serverInterface.getXmlFromUrl(domain + query + username + password);
	      if(response==null)
	      {
	    	  Toast.makeText(MainActivity.this, "null", 2000).show();
	      }
	      return response;
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	process(result);
	    }
	}
	
	public void process(String XML) {
		String status = "", result = "";
		boolean understood = false;
		boolean answered = false;
		doc = myParser.getDomElement(XML);
		NodeList nl = doc.getElementsByTagName("tk:response");	
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			status = myParser.getValue(e, "tk:status");
			understood = e.getAttribute("understood").equals("true");
			answered = e.getAttribute("answered").equals("true");
			result = myParser.getValue(e, "tk:text_result");
		}
		//txtStatus.setText("Status: " + status);
		//txtUnderstood.setText("Understood: " + understood);
		//txtAnswered.setText("Answered: " + answered);
		//txtResult.setText(result);
		//tts.speak(new Response(true,true,"Hello","world"));
		//tts.speak(new Response((boolean)understood, (boolean)answered, result, status));
		tts.preSpeak(result);
	
		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		while(tts.tts.isSpeaking())
		{
			//voiceSearch();
			
		}
		voiceSearch();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == code && resultCode == RESULT_OK) {
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String result = results.get(0);
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
			choice(result);
			//call(result);
		}
	}
	
	@Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            //tts.stop();
        }
        super.onDestroy();
    }
	
}