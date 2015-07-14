package ch.ethz.soms.nervous.competition.model;

import java.io.Serializable;


public class AccReading extends Reading implements Serializable {

	public double x, y, z;
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
