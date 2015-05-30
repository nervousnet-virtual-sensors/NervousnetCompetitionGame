import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;

import com.example.nervousco.AccReading;
import com.example.nervousco.LightReading;
import com.example.nervousco.Reading;

public class Competition {

	public final static int COMPETITION_TYPE_ACCELEROMETER = 0;
	public final static int COMPETITION_TYPE_LIGHT= 1;
	
	public int COMPETITION_TYPE = COMPETITION_TYPE_ACCELEROMETER;
	
	double[] team = new double[] { 0, 0 };
	ArrayList<Reading> readings = new ArrayList<Reading>();
	AccReading[] lastReadings = new AccReading[] {
			new AccReading(0, 0, 0, 0, 0), new AccReading(0, 0, 0, 0, 1) };

	LightReading[] lastLightReadings = new LightReading[] {
			new LightReading(0, 0, 0), new LightReading(0, 0, 0) };

	public synchronized void reset() {
		team = new double[] { 0, 0 };
		readings.clear();
		lastReadings =  new AccReading[] {
				new AccReading(0, 0, 0, 0, 0), new AccReading(0, 0, 0, 0, 1) };
		lastLightReadings = new LightReading[] {
				new LightReading(0, 0, 0), new LightReading(0, 0, 0) };
	}

	public synchronized void pushReading(AccReading r) {
		readings.add(r);

		lastReadings[r.team] = r;

		int t = r.team;
//		for (int t = 0; t <= 1; t++) {
			team[t] += Math.abs(lastReadings[t].x)
					+ Math.abs(lastReadings[t].y)
					+ Math.abs(lastReadings[t].z);
			
//			System.out.println("Score Team "+t+" - "+team[t]);
//		}
		
		
		
//		System.out.println("lastReadings");
	}

	public synchronized void pushReading(LightReading r) {
		readings.add(r);

		lastLightReadings[r.team] = r;

		int t = r.team;
//		for (int t = 0; t <= 1; t++) {
			team[t] += lastLightReadings[t].lightVal;
//			System.out.println("Score Team "+t+" - "+team[t]);
//		}
	}

	public synchronized double getScore() {
		if (team[0] + team[1] == 0) {
			return 0.5;
		}
		return 0.5 + (team[1] - team[0]) / (team[0] + team[1]) / 2;
	}
	
	public synchronized double[] getLastReadings() {
		double[] teamScore = new double[] { 0, 0 };
		if (COMPETITION_TYPE == COMPETITION_TYPE_ACCELEROMETER){
			
			for (int t = 0; t <= 1; t++) {
				teamScore[t] += Math.abs(lastReadings[t].x)
						+ Math.abs(lastReadings[t].y)
						+ Math.abs(lastReadings[t].z);
				System.out.println("Team Score Accel "+teamScore[t]);
			}
			
			
		}else {
			for (int t = 0; t <= 1; t++) {
				teamScore[t] += lastLightReadings[t].lightVal;
				System.out.println("Team Score Light "+teamScore[t]);
			}
			
		}
		return teamScore;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<Reading> getReadings() {
		return (List<Reading>) readings.clone();
	}
}

// import java.util.Timer;
// import java.util.TimerTask;
//
// import com.example.nervousco.AccReading;
//
// public class Competition {
//
// private int duration;
// private int playDur;
// private double startingTime = 0;
// private CompPlayer player;
// private Timer timer;
//
// public Competition(int duration, CompPlayer player, int playDur) {
// this.duration = duration;
// this.player = player;
// this.playDur = playDur;
// timer = new Timer();
// }
//
// class Play extends TimerTask {
// @Override
// public void run() {
// player.play(getScore() >= 0.5? 1 : 0, playDur);
// }
// }
//
// public synchronized void reset() {
// timer.cancel();
// player.pause();
// timer = new Timer();
// timer.schedule(new Play(), duration*1000);
// startingTime = getTime();
// }
//
// public double getDuration() {
// return duration;
// }
//
// public synchronized void pushReading(AccReading reading) {
// System.out.println(reading);
// }
//
// private double getTime() {
// return (double)System.nanoTime()/1000000000;
// }
//
// public synchronized double getTimeRemaining() {
//
// double remTime = duration - getTime() + startingTime;
// System.out.println(duration);
// if(remTime <= 0.1) {
// return 0;
// } else {
// return remTime;
// }
// }
//
// public synchronized double getScore() {
// return Math.random();
// }
// }
