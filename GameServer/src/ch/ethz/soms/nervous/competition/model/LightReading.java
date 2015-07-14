package ch.ethz.soms.nervous.competition.model;

import java.io.Serializable;


public class LightReading extends Reading implements Serializable {

	public double lightVal;
	
	public LightReading(double lightVal, long timestamp, int team) {
		this.lightVal = lightVal;
		this.timestamp = timestamp;
		this.team = team;
		serialVersionUID = 2L;
	}

	public String toString() {
		return "(" + team + "," + timestamp + ") -> " + "(" + lightVal + ")";
	}
}
