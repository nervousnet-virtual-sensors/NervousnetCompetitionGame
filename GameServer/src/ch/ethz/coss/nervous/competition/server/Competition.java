package ch.ethz.coss.nervous.competition.server;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import ch.ethz.coss.nervous.competition.model.AccReading;
import ch.ethz.coss.nervous.competition.model.LightReading;
import ch.ethz.coss.nervous.competition.model.NoiseReading;
import ch.ethz.coss.nervous.competition.model.Reading;


public class Competition {

	public final static int COMPETITION_TYPE_ACCELEROMETER = 0;
	public final static int COMPETITION_TYPE_LIGHT = 1;
	public final static int COMPETITION_TYPE_NOISE = 2;

	public int COMPETITION_TYPE = COMPETITION_TYPE_ACCELEROMETER;

	double[] team = new double[] { 0, 0 };
	ArrayList<Reading> readings = new ArrayList<Reading>();
	AccReading[] lastReadings = new AccReading[] {
			new AccReading(0, 0, 0, 0, 0), new AccReading(0, 0, 0, 0, 1) };

	LightReading[] lastLightReadings = new LightReading[] {
			new LightReading(0, 0, 0), new LightReading(0, 0, 0) };

	NoiseReading[] lastNoiseReadings = new NoiseReading[] {
			new NoiseReading(0, 0, 0), new NoiseReading(0, 0, 0) };
	
	Hashtable<String, Reading> hUniquePlayersRedTeam = new Hashtable<String, Reading>(); // key is the android_id
														// and value is the last
														// reading
	Hashtable<String, Reading> hUniquePlayersGreenTeam = new Hashtable<String, Reading>();

	public synchronized void reset() {
		team = new double[] { 0, 0 };
		readings.clear();
		hUniquePlayersRedTeam.clear();
		hUniquePlayersGreenTeam.clear();
		lastReadings = new AccReading[] { new AccReading(0, 0, 0, 0, 0),
				new AccReading(0, 0, 0, 0, 1) };
		lastLightReadings = new LightReading[] { new LightReading(0, 0, 0),
				new LightReading(0, 0, 0) };
	}

	public synchronized void pushReading(AccReading r) {
		readings.add(r);
		lastReadings[r.team] = r;

		if (r.team == 0) {
			hUniquePlayersGreenTeam.put(r.android_id, r);

			if (hUniquePlayersRedTeam.containsKey(r.android_id))
				hUniquePlayersRedTeam.remove(r.android_id);
		} else {
			hUniquePlayersRedTeam.put(r.android_id, r);

			if (hUniquePlayersGreenTeam.containsKey(r.android_id))
				hUniquePlayersGreenTeam.remove(r.android_id);
		}

		int t = r.team;
		// for (int t = 0; t <= 1; t++) {
		team[t] += Math.abs(lastReadings[t].x) + Math.abs(lastReadings[t].y)
				+ Math.abs(lastReadings[t].z);

		// System.out.println("Score Team "+t+" - "+team[t]);
		// }

		// System.out.println("lastReadings");
	}

	public synchronized void pushReading(LightReading r) {
		readings.add(r);

		lastLightReadings[r.team] = r;

		
		if (r.team == 0) {
			hUniquePlayersGreenTeam.put(r.android_id, r);

			if (hUniquePlayersRedTeam.containsKey(r.android_id))
				hUniquePlayersRedTeam.remove(r.android_id);
		} else {
			hUniquePlayersRedTeam.put(r.android_id, r);

			if (hUniquePlayersGreenTeam.containsKey(r.android_id))
				hUniquePlayersGreenTeam.remove(r.android_id);
		}
		
		int t = r.team;

		// for (int t = 0; t <= 1; t++) {
		team[t] += lastLightReadings[t].lightVal;
		
		// }
	}
	
	public synchronized void pushReading(NoiseReading r) {
		readings.add(r);

		lastNoiseReadings[r.team] = r;

		
		if (r.team == 0) {
			hUniquePlayersGreenTeam.put(r.android_id, r);

			if (hUniquePlayersRedTeam.containsKey(r.android_id))
				hUniquePlayersRedTeam.remove(r.android_id);
		} else {
			hUniquePlayersRedTeam.put(r.android_id, r);

			if (hUniquePlayersGreenTeam.containsKey(r.android_id))
				hUniquePlayersGreenTeam.remove(r.android_id);
		}
		
		int t = r.team;

		// for (int t = 0; t <= 1; t++) {
		team[t] += lastNoiseReadings[t].soundVal;
		
		// }
	}

	public synchronized double getScore() {
		if (team[0] + team[1] == 0) {
			return 0.5;
		}
		return 0.5 + (team[1] - team[0]) / (team[0] + team[1]) / 2;
	}

	// public synchronized double[] getLastReadings() {
	// double[] teamScore = new double[] { 0, -0 };
	// if (COMPETITION_TYPE == COMPETITION_TYPE_ACCELEROMETER){
	//
	// for (int t = 0; t <= 1; t++) {
	// teamScore[t] = Math.abs(lastReadings[t].x)
	// + Math.abs(lastReadings[t].y)
	// + Math.abs(lastReadings[t].z);
	//
	// if(teamScore[t] == 0)
	// teamScore[t] = -1;
	// System.out.println("Team Score Accel "+teamScore[t]);
	// }
	//
	//
	//
	// }else {
	// for (int t = 0; t <= 1; t++) {
	// teamScore[t] = lastLightReadings[t].lightVal;
	// if(teamScore[t] == 0)
	// teamScore[t] = -1;
	// System.out.println("Team Score Light "+
	// (t==0?"GREEN":"RED")+" Score =  "+ teamScore[t]);
	// }
	//
	// }
	// return teamScore;
	// }

	public synchronized double[] getLastReadings() {
		double[] teamScore = new double[] { 0, 0 };
		
		Enumeration<Reading> e = hUniquePlayersGreenTeam.elements();		
		
		while(e.hasMoreElements()){
			Reading r = (Reading) e.nextElement();
			
			if(COMPETITION_TYPE == COMPETITION_TYPE_ACCELEROMETER){
				if(r instanceof AccReading){
					AccReading accR = (AccReading) r;
					
					if(accR.team == 0)
					teamScore[0] += Math.abs(accR.x) + Math.abs(accR.y)
							+ Math.abs(accR.z);
					
				}
			}else if(COMPETITION_TYPE == COMPETITION_TYPE_LIGHT){
				if(r instanceof LightReading){
					LightReading lightR = (LightReading) r;
					
					if(lightR.team == 0)
					teamScore[0] += lightR.lightVal;
					
				}
			}else if(COMPETITION_TYPE == COMPETITION_TYPE_NOISE){
				if(r instanceof NoiseReading){
					NoiseReading noiseR = (NoiseReading) r;
					
					if(noiseR.team == 0)
					teamScore[0] += noiseR.soundVal;
					
				}
			}
		
		}
		
		
Enumeration<Reading> e2 = hUniquePlayersRedTeam.elements();		
		
		while(e2.hasMoreElements()){
			Reading r = (Reading) e2.nextElement();
			
			if(COMPETITION_TYPE == COMPETITION_TYPE_ACCELEROMETER){
				if(r instanceof AccReading){
					AccReading accR = (AccReading) r;
					
					if(accR.team == 1)
					teamScore[1] += Math.abs(accR.x) + Math.abs(accR.y)
							+ Math.abs(accR.z);
					
				}
			}else if(COMPETITION_TYPE == COMPETITION_TYPE_LIGHT){
				if(r instanceof LightReading){
					LightReading lightR = (LightReading) r;
					
					if(lightR.team == 1)
					teamScore[1] += lightR.lightVal;
					
				}
			}else if(COMPETITION_TYPE == COMPETITION_TYPE_NOISE){
				if(r instanceof NoiseReading){
					NoiseReading noiseR = (NoiseReading) r;
					
					if(noiseR.team == 1)
					teamScore[1] += noiseR.soundVal;
					
				}
			}
		
		}

		
//		System.out.println("GREEEN TEAM -- "+teamScore[0]+", RED TEAM -- "+teamScore[1]);
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
