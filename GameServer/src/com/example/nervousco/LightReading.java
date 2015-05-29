package com.example.nervousco;

import java.io.Serializable;

public class LightReading extends Reading implements Serializable {

	public long timestamp;
	public double lightVal;
	public int team;

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
