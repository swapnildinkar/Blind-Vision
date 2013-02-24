package com.example.demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.telephony.TelephonyManager;

public class ServerInterface {
	DefaultHttpClient httpClient;
	HttpGet httpGet;
	HttpResponse httpResponse;
	HttpEntity httpEntity;

	public String getJSONFromUrl(String url) {
		String json = null;
		try {
			
			// defaultHttpClient
			httpClient = new DefaultHttpClient();
			httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			json = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return json;
	}

	public String register(Context context) {
		String result = null;
		try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String phonenumber = tm.getSimSerialNumber();
			httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("mess.byethost31.com/bv/mreg.php?phn="+phonenumber);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			result = EntityUtils.toString(httpEntity);
			if (result.equals("done")) {
				return "success";
			} else {
				return "failed";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
}
