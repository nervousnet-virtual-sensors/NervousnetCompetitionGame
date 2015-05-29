package com.example.nervousco;

import java.io.Serializable;

public class AccReading extends Reading implements Serializable {

	public long timestamp;
	public double x, y, z;
	public int team;

	public AccReading(double x, double y, double z, long timestamp, int team) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.timestamp = timestamp;
		this.team = team;
		serialVersionUID = 1L;
	}

	public String toString() {
		return "(" + team + "," + timestamp + ") -> " + "(" + x + "," + y + ","
				+ z + ")";
	}
}
