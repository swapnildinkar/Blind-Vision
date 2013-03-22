package com.blindvision.main;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

public class IOIOUltrasonicSensorActivity extends AbstractIOIOActivity {
	/* ultrasonic sensor */
	private int echoSeconds1,echoSeconds2,echoSeconds3;
	private int echoDistanceCm1, echoDistanceCm2, echoDistanceCm3;

	Vibrator v;
	/**
	 * Called upon creation for initialization
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
		System.out.println("Inside US activity");
		/* ultrasonic sensor */
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		v.cancel();
	}

	/**
	 * Primary thread...runs until interrupted
	 */
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private DigitalOutput triggerPin1_;
		private PulseInput echoPin1_;
		private DigitalOutput triggerPin2_;
		private PulseInput echoPin2_;
		private DigitalOutput triggerPin3_;
		private PulseInput echoPin3_;
		@SuppressWarnings("deprecation")
		public void setup() throws ConnectionLostException {
			try {
				echoPin1_ = ioio_.openPulseInput(5, PulseMode.POSITIVE);
				triggerPin1_ = ioio_.openDigitalOutput(7);
				echoPin2_ = ioio_.openPulseInput(9, PulseMode.POSITIVE);
				triggerPin2_ = ioio_.openDigitalOutput(11);
				echoPin3_ = ioio_.openPulseInput(13, PulseMode.POSITIVE);
				triggerPin3_ = ioio_.openDigitalOutput(15);
				Log.v("Blind Vision", "inside setup");
			} catch (ConnectionLostException e) {
				throw e;
			}
		}
		public void loop() throws ConnectionLostException {
			try {
				System.out.println("Inside Loop");
				/*triggerPin1_.write(false);
				sleep(5);
				triggerPin1_.write(true);
				sleep(1);
				triggerPin1_.write(false);
				echoSeconds1 = (int) (echoPin1_.getDuration() * 1000 * 1000);
				echoDistanceCm1 = echoSeconds1 / 29 / 2;
			    checkDistance();
				sleep(20);
				triggerPin2_.write(false);
				sleep(5);
				triggerPin2_.write(true);
				sleep(1);
				triggerPin2_.write(false);
				echoSeconds2 = (int) (echoPin2_.getDuration() * 1000 * 1000);
				echoDistanceCm2 = echoSeconds2 / 29 / 2;
				checkDistance();
				sleep(20);
				*/triggerPin3_.write(false);
				sleep(5);
				triggerPin3_.write(true);
				sleep(1);
				triggerPin3_.write(false);
				echoSeconds3 = (int) (echoPin3_.getDuration() * 1000 * 1000);
				echoDistanceCm3 = echoSeconds3 / 29 / 2;
				checkDistance();
				sleep(20);
				Log.v("Blind Vision", echoDistanceCm1 + ", " + echoDistanceCm2 + ", " + echoDistanceCm3);
				sleep(1000);
				
			} catch (InterruptedException e) {
				ioio_.disconnect();

			} catch (ConnectionLostException e) {
				throw e;
			}
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
	private void checkDistance() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.v("Blind Vision", echoDistanceCm1 + ", " + echoDistanceCm2 + ", " + echoDistanceCm3);
				if(echoDistanceCm1<50) {
					v.vibrate(new long[]{0,  200, 200}, -1);
				}
				else if(echoDistanceCm2<50) {
					v.vibrate(new long[]{0,  1000, 0}, 0);
				}
				else if(echoDistanceCm3<50) {
					v.vibrate(new long[]{0,  300, 100, 100, 300}, -1);
				}
				else {
					v.cancel();
				}
			}
		});
	}
}
