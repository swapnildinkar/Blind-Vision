package com.example.demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;

public class ServerInterface {
	DefaultHttpClient httpClient;
	HttpGet httpGet;
	HttpResponse httpResponse;
	HttpEntity httpEntity;

	static String SERVER_URL = "http://mess.byethost31.com/bv/";

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
		String result = null, reply = null, bid = null, bkey = null;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			String phonenumber = tm.getSimSerialNumber();
			httpClient = new DefaultHttpClient();
			// HttpGet httpGet = new
			// HttpGet("mess.byethost31.com/bv/mreg.php?phn="+phonenumber);
			httpGet = new HttpGet(
					"http://www.mess.byethost31.com/bv/mreg.php?phn="
							+ phonenumber);
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			reply = EntityUtils.toString(httpEntity);
			return reply;
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

	public static String setLocation(String data) {
		SERVER_URL += "mcon.php";
		return executeHttpRequest(data);
	}

	private static String executeHttpRequest(String data) {
		String result = "";
		try {
			URL url = new URL(SERVER_URL);
			URLConnection connection = url.openConnection();

			/*
			 * We need to make sure we specify that we want to provide input and
			 * get output from this connection. We also want to disable caching,
			 * so that we get the most up-to-date result. And, we need to
			 * specify the correct content type for our data.
			 */
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			// Send the POST data
			DataOutputStream dataOut = new DataOutputStream(
					connection.getOutputStream());
			dataOut.writeBytes(data);
			dataOut.flush();
			dataOut.close();

			// get the response from the server and store it in result
			DataInputStream dataIn = new DataInputStream(
					connection.getInputStream());
			String inputLine;
			while ((inputLine = dataIn.readLine()) != null) {
				result += inputLine;
			}
			dataIn.close();
		} catch (IOException e) {
			/*
			 * In case of an error, we're going to return a null String. This
			 * can be changed to a specific error message format if the client
			 * wants to do some error handling. For our simple app, we're just
			 * going to use the null to communicate a general error in
			 * retrieving the data.
			 */
			e.printStackTrace();
			result = null;
		}

		return result;
	}
}
