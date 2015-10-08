package ch.ethz.coss.nervous.competition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import ch.ethz.coss.nervous.competition.sensor.NoiseSensor;
import ch.ethz.coss.nervous.competition.R;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

@SuppressLint({ "Wakelock", "InlinedApi" })
public class MainActivity extends Activity {

	public static final String DEBUG_TAG = "MainActivityCo";


	TextView tv_team, tv_server, tv_port, tv_sensor;
	// Button settingsButton;

	WakeLock wakeLock;
	WifiLock wifiLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_team = (TextView) findViewById(R.id.tv_team);
		tv_sensor = (TextView) findViewById(R.id.tv_sensor);
		tv_server = (TextView) findViewById(R.id.tv_server);
		tv_port = (TextView) findViewById(R.id.tv_port);

		// settingsButton = (Button) findViewById(R.id.action_settings);
		// settingsButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainActivity.this,
		// PrefActivity.class);
		// startActivity(intent);
		//
		// }
		// });
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
		wakeLock.acquire();

		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,
				"WifyLock");
		wifiLock.acquire();

		try {
			registerTask();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	SynchWriter synchout;
	SensorService readingTask;
	SensorManager sensorManager;

	private OnSharedPreferenceChangeListener listener;

	protected synchronized void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		if (sensorManager != null)
			sensorManager.unregisterListener(readingTask);
		if (synchout != null)
			synchout.stop();
		if (wakeLock != null)
			wakeLock.release();
		if (wifiLock != null)
			wifiLock.release();
		super.onDestroy();
	}

	public synchronized void registerTask() throws IOException {

		Log.d(DEBUG_TAG, "register");
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// final Sensor sensorAccelerometer = sensorManager
		// .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//
		// final Sensor sensorLight = sensorManager
		// .getDefaultSensor(Sensor.TYPE_LIGHT);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		int sensor = Integer
				.parseInt(sp.getString("sensorType", Constants.defaultSensor));

		if (sensor == Constants.SENSOR_TYPE_ACCELEROMETER) {
			tv_sensor.setText("Accelerometer");
		} else if (sensor == Constants.SENSOR_TYPE_LIGHT)  {
			tv_sensor.setText("Light");
		} else if (sensor == Constants.SENSOR_TYPE_NOISE)  {
			tv_sensor.setText("Noise");
		}


		String address = sp.getString("server", Constants.defaultServer);
		tv_server.setText(address);
		int port = Integer.parseInt(sp.getString("port", Constants.defaultPort));
		tv_port.setText("" + port);
		int team = Integer.parseInt(sp.getString("team", Constants.defaultTeam));

		if (team == Constants.TEAM_GREEN) {
			tv_team.setTextColor(Color.GREEN);
			tv_team.setText("Green");
		} else {
			tv_team.setTextColor(Color.RED);
			tv_team.setText("Red");
		}

		int writingInterval = Integer.parseInt(sp.getString("writinginterval",
				Constants.defaultWritingInterval));

		synchout = new SynchWriter(address, port, writingInterval);
		synchout.setSensorType(sensor);
		readingTask = new SensorService(synchout, team, writingInterval,
				MainActivity.this);

		if (sensor == Constants.SENSOR_TYPE_ACCELEROMETER) {
			sensorManager.registerListener(readingTask,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_GAME);
		} else if (sensor == Constants.SENSOR_TYPE_LIGHT){
			sensorManager.registerListener(readingTask,
					sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
					SensorManager.SENSOR_DELAY_GAME);
		} else if (sensor == Constants.SENSOR_TYPE_NOISE){
			NoiseSensor  sensorNoise = new NoiseSensor();
			System.out.println("Sensor Noise activated");
			sensorNoise.clearListeners();
			sensorNoise.addListener(readingTask);
			// Noise sensor doesn't really make sense with less than 500ms
			sensorNoise.startRecording(500);
		
			
		}

		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				String val = sharedPreferences.getString(key, null);

				if (key.equals("sensorType")) {
					synchout.setSensorType(Integer.valueOf(val));

					if (tv_sensor != null)
						if (Integer.valueOf(val) == 0) {
							tv_sensor.setText("Accelerometer");
							if (sensorManager != null) {
								sensorManager
										.unregisterListener(
												readingTask,
												sensorManager
														.getDefaultSensor(Sensor.TYPE_LIGHT));

								sensorManager = null;
							}

							sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
							sensorManager
									.registerListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
											SensorManager.SENSOR_DELAY_GAME);

						} else if (Integer.valueOf(val) == 1) {
							tv_sensor.setText("Light");
							if (sensorManager != null) {
								sensorManager
										.unregisterListener(
												readingTask,
												sensorManager
														.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
								sensorManager = null;
							}
							sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

							sensorManager
									.registerListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_LIGHT),
											SensorManager.SENSOR_DELAY_GAME);
						} else if (Integer.valueOf(val) == 2) {
							tv_sensor.setText("Noise");
							if (sensorManager != null) {
							sensorManager
									.unregisterListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
							sensorManager
									.unregisterListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_LIGHT));
							sensorManager = null;
							}
							// sensorManager = (SensorManager)
							// getSystemService(SENSOR_SERVICE);

							// sensorManager
							// .registerListener(
							// readingTask,
							// sensorManager
							// .getDefaultSensor(Sensor.TYPE_LIGHT),
							// SensorManager.SENSOR_DELAY_GAME);
						}
				}

				if (key.equals("server")) {
					synchout.setServerAddress(val);
					if (tv_server != null)
						tv_server.setText(val);
					else
						System.out.println("Server TextView is null");
				}
				if (key.equals("port")) {
					synchout.setServerPort(Integer.valueOf(val));
				}
				if (key.equals("team")) {
					readingTask.setTeam(Integer.valueOf(val));

					if (tv_team != null)
						if (Integer.valueOf(val) == 0) {
							tv_team.setTextColor(Color.GREEN);
							tv_team.setText("Green");
						} else {
							tv_team.setTextColor(Color.RED);
							tv_team.setText("Red");
						}
				}
				if (key.equals("writinginterval")) {
					synchout.setWritingInterval(Integer.valueOf(val));
				}

				// System.out.println("Setting: " + key + ", Value = " + val);
			}
		};

		sp.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, PrefActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPause() {
		Log.d(DEBUG_TAG, "onPause");
		super.onPause();
	}

	public void onResume() {
		Log.d(DEBUG_TAG, "onResume");
		super.onResume();
	}

	public void onStop() {
		Log.d(DEBUG_TAG, "onStop");
		super.onStop();
	}

	public void onStart() {
		Log.d(DEBUG_TAG, "onStart");
		super.onStart();
	}
}
