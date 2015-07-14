package ch.ethz.soms.nervous.competition.model;

import java.io.Serializable;


public class NoiseReading extends Reading implements Serializable {

	public double soundVal;
	
	public NoiseReading(double soundVal, long timestamp, int team) {
		this.soundVal = soundVal;
		this.timestamp = timestamp;
		this.team = team;
		serialVersionUID = 2L;
	}

	public String toString() {
		return "(" + team + "," + timestamp + ") -> " + "(" + soundVal + ")";
	}
}
