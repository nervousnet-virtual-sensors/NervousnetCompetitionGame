package com.example.nervousco;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class SensorService implements SensorEventListener {

	private SynchWriter out;
	private int team;
	private SensorEvent event;
	private SensorValuePusherTask sensorValuePusherTask;
	Timer timer;
	
	public SensorService(SynchWriter out, int team, long writingInterval, Context context) {
		this.out = out;
		this.team = team;
		timer = new Timer();
		sensorValuePusherTask = new SensorValuePusherTask(context);
		timer.schedule(sensorValuePusherTask, writingInterval, writingInterval);
	}

	private static final String DEBUG_TAG = "NervousGameService";

	@Override
	public synchronized void onSensorChanged(SensorEvent event) {
		// System.out.println("OnSensorChanged called");

		this.event = event;

	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	class SensorValuePusherTask extends TimerTask {

		Context context;
		
		public SensorValuePusherTask(Context context){
			this.context = context;
		}
		
		
		public synchronized void run() {
			
			if(event != null)
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// System.out.println("OnSensorChanged called for "+
				// event.sensor.getName()+"  - "+event.values[0]+", "+event.values[0]+", "+event.values[0]);
				AccReading reading = new AccReading(event.values[0],
						event.values[1], event.values[2],
						System.currentTimeMillis(), team);
				reading.android_id = UniqueIDHandler.getIMEI(context);
				out.send(reading);
				Log.d(DEBUG_TAG, reading.toString());
			} else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				// System.out.println("OnSensorChanged called for "+
				// event.sensor.getName()+" - "+ event.values[0]);
				LightReading reading = new LightReading(event.values[0],
						System.currentTimeMillis(), team);
				reading.android_id = UniqueIDHandler.getIMEI(context);
				out.send(reading);
				Log.d(DEBUG_TAG, reading.toString());
			} else {
//				System.out.println("OnSensorChanged called. But unknown Sensor "
//						+ event.sensor.getName());
			}
		}
		
		synchronized void stop() {
		
		}
	}
	
	public void stop() {
		timer.cancel();
		sensorValuePusherTask.stop();
	}
}
