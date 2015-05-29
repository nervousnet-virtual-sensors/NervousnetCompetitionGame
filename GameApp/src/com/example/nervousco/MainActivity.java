package com.example.nervousco;

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

	public static String defaultServer = "10.3.25.32"; // emulator host
														// localhost
	public static String defaultPort = "8080";
	public static String defaultWritingInterval = "100";
	public static String defaultTeam = "0";
	public static String defaultSensor = "0";

	TextView tv_team, tv_server, tv_port, tv_sensor;
	Button settingsButton;

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

		settingsButton = (Button) findViewById(R.id.action_settings);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						PrefActivity.class);
				startActivity(intent);

			}
		});
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
				.parseInt(sp.getString("sensorType", defaultSensor));

		if (sensor == 0) {
			tv_sensor.setText("Accelerometer");
		} else {
			tv_sensor.setText("Light");
		}

		String address = sp.getString("server", defaultServer);
		tv_server.setText(address);
		int port = Integer.parseInt(sp.getString("port", defaultPort));
		tv_port.setText("" + port);
		int team = Integer.parseInt(sp.getString("team", defaultTeam));

		if (team == 0) {
			tv_team.setTextColor(Color.GREEN);
			tv_team.setText("Green");
		} else {
			tv_team.setTextColor(Color.RED);
			tv_team.setText("Red");
		}

		int writingInterval = Integer.parseInt(sp.getString("writinginterval",
				defaultWritingInterval));

		synchout = new SynchWriter(address, port, writingInterval);
		synchout.setSensorType(sensor);
		readingTask = new SensorService(synchout, team);

		if (sensor == 0) {
			sensorManager.registerListener(readingTask,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			sensorManager.registerListener(readingTask,
					sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
					SensorManager.SENSOR_DELAY_NORMAL);
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
							System.out.println("Unregister Light");
							sensorManager
									.unregisterListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_LIGHT));
							System.out.println("After Unregister Light");
							sensorManager = null;
							sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
							sensorManager
									.registerListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
											SensorManager.SENSOR_DELAY_NORMAL);
						} else {
							tv_sensor.setText("Light");
							System.out.println("Unregister Accelerometer");
							sensorManager
									.unregisterListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
							sensorManager = null;
							sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

							System.out
									.println("after Unregister Accelerometer");
							sensorManager
									.registerListener(
											readingTask,
											sensorManager
													.getDefaultSensor(Sensor.TYPE_LIGHT),
											SensorManager.SENSOR_DELAY_NORMAL);
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

				System.out.println("Setting: " + key + ", Value = " + val);
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
