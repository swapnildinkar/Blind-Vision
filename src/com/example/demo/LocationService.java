package com.example.demo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	String bid;
	public static boolean linstance;
	String pass;
	LocationManager lm;
	LocationListener locationlistener;
	String locMsg;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		linstance=true;
		Toast.makeText(this, "Blind Vision Service Created!", Toast.LENGTH_SHORT)
				.show();
		SharedPreferences file = this.getSharedPreferences(
				"blindvisionprefs", 0);
		bid = file.getString("bid", "");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Blind Vision Location Service Stopped!", Toast.LENGTH_SHORT)
				.show();
		lm.removeUpdates(locationlistener);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		getLocation();
		Toast.makeText(this, "Blind Vision Location Service Started", Toast.LENGTH_SHORT)
				.show();
	}

	private void getLocation() {
		lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationlistener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					
					locMsg = "http://mess.byethost31.com/bv/mloc.php?id=" + bid
							+ "&lat=" + location.getLatitude() + "&lng="
							+ location.getLongitude();
					new UpdateTask().execute();
					Toast.makeText(LocationService.this,"Location Updated"+ locMsg, Toast.LENGTH_SHORT)
							.show();
				}
			}

			public void onProviderDisabled(String provider) {
				String locMsg = "\nGPS provider disabled";
				Log.d("Blind Vision", "BootReceiver: Provider disabled");
				Toast.makeText(LocationService.this, "GPS Disabled",
						Toast.LENGTH_SHORT).show();				
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};
		lm.requestLocationUpdates("gps", 1 * 60 * 1000, 10, locationlistener);
	}
	
	private class UpdateTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String result = new ServerInterface().getJSONFromUrl(locMsg);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(LocationService.this, result,
					Toast.LENGTH_SHORT).show();
		}
	}
}
