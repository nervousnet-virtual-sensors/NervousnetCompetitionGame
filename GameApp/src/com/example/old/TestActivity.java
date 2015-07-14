package com.example.old;

import java.io.IOException;

import ch.ethz.soms.nervous.competition.SynchWriter;
import ch.ethz.soms.nervous.competition.model.AccReading;
import ch.ethz.soms.nervous.competition.model.LightReading;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class TestActivity extends Activity {

	public static final String DEBUG_TAG = "MainActivity";

	static int writingInterval = 100;
	static String ipAddress = "10.3.25.32";
	static int port = 8080;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (int i = 0; i < 100; i++) {

			final int ii = i;

			new Thread(new Runnable() {

				@Override
				public void run() {

					SynchWriter out = null;
					try {
						out = new SynchWriter(ipAddress, port, writingInterval);
					} catch (IOException e) {
						e.printStackTrace();
					}

					for (int j = 0; j < 100; j++) {
						Log.d(DEBUG_TAG, ii + "" + j);
						// out.send(new AccReading(0,0,0,ii,j)); pp
						out.send(new LightReading(0.0, ii, j));
					}
				}

			}).start();
		}
	}
}