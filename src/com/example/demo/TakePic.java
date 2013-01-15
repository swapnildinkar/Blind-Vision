package com.example.demo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

	public class TakePic extends Activity {
		private static final String TAG = "CameraDemo";
		Camera camera;
		Button buttonClick;
		SurfaceView surface;

		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_ocr);
			camera = Camera.open();
			surface = new SurfaceView(this);
			//((FrameLayout) findViewById(R.id.preview)).addView(surface);
			//camera.startPreview();
			//buttonClick = (Button) findViewById(R.id.bu//ttonClick);
			//buttonClick.setOnClickListener(new OnClickListener() {
			//public void onClick(View v) {
					camera.startPreview();
					camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				//}
			//});
					finish();
					startActivity(new Intent(this, MainActivity.class));
			Log.d(TAG, "onCreate'd");
		}

		ShutterCallback shutterCallback = new ShutterCallback() {
			public void onShutter() {
				Log.d(TAG, "onShutter'd");
			}
		};

		/** Handles data for raw picture */
		PictureCallback rawCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				Log.d(TAG, "onPictureTaken - raw");
			}
		};

		/** Handles data for jpeg picture */
		PictureCallback jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				FileOutputStream outStream = null;
				try {
					// write to local sandbox file system
					// outStream =
					// CameraDemo.this.openFileOutput(String.format("%d.jpg",
					// System.currentTimeMillis()), 0);
					// Or write to sdcard
					outStream = new FileOutputStream(String.format(
							"/sdcard/%d.jpg", System.currentTimeMillis()));
					outStream.write(data);
					outStream.close();
					Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				Log.d(TAG, "onPictureTaken - jpeg");
			}
		};

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if(camera!=null)
			camera.release();
			
		}
		
		

}
