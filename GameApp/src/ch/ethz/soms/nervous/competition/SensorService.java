package ch.ethz.soms.nervous.competition;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.soms.nervous.competition.model.AccReading;
import ch.ethz.soms.nervous.competition.model.LightReading;
import ch.ethz.soms.nervous.competition.model.NoiseReading;
import ch.ethz.soms.nervous.competition.model.Reading;
import ch.ethz.soms.nervous.competition.sensor.NoiseSensor;
import ch.ethz.soms.nervous.competition.sensor.NoiseSensor.NoiseListener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class SensorService implements SensorEventListener, NoiseListener {
	private static final String DEBUG_TAG = "NervousGameService";
	
	private SynchWriter out;
	private int team;
	private SensorEvent event;
	private NoiseReading noiseReading;
	private SensorValuePusherTask sensorValuePusherTask;
	Timer timer;
	
	public SensorService(SynchWriter out, int team, long writingInterval, Context context) {
		this.out = out;
		this.team = team;
		timer = new Timer();
		sensorValuePusherTask = new SensorValuePusherTask(context);
		timer.schedule(sensorValuePusherTask, writingInterval, writingInterval);
	}



	@Override
	public synchronized void onSensorChanged(SensorEvent event) {
		// System.out.println("OnSensorChanged called");

		this.event = event;

	}
	/*
	 * (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	@Override
	public void noiseSensorDataReady(long timestamp, float rms, float spl, float[] bands) {
		noiseReading = new NoiseReading(spl,timestamp,team);
		Log.d(DEBUG_TAG, "Noise data collected");
	}
	
	class SensorValuePusherTask extends TimerTask {

		Context context;
		
		public SensorValuePusherTask(Context context){
			this.context = context;
		}
		
		
		public synchronized void run() {
			
			if(event != null){
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					Reading reading = new AccReading(event.values[0],
							event.values[1], event.values[2],
							System.currentTimeMillis(), team);
					reading.setAndroidID(UniqueIDHandler.getIMEI(context));
					out.send(reading);
					Log.d(DEBUG_TAG, reading.toString());
				} else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
					LightReading reading = new LightReading(event.values[0],
							System.currentTimeMillis(), team);
					reading.setAndroidID(UniqueIDHandler.getIMEI(context));
					out.send(reading);
					Log.d(DEBUG_TAG, reading.toString());
				} else {
					
					System.out.println("OnSensorChanged called. But unknown Sensor "
							+ event.sensor.getName());
				}
				event = null;
			}else{
				
				if(noiseReading != null){
					noiseReading.setAndroidID(UniqueIDHandler.getIMEI(context));
					out.send(noiseReading);
					Log.d(DEBUG_TAG, noiseReading.toString());
					
					NoiseSensor  sensorNoise = new NoiseSensor();
					System.out.println("Sensor Noise activated");
					sensorNoise.clearListeners();
					sensorNoise.addListener(SensorService.this);
					// Noise sensor doesn't really make sense with less than 500ms
					sensorNoise.startRecording(500);
				}
			
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
