package ch.ethz.soms.nervous.competition.model;

import java.io.Serializable;

public class Reading implements Serializable {
	protected long serialVersionUID;
	public String android_id;
	public long timestamp;
	public int team;
	public String getAndroidID() {
		return android_id;
	}
	public void setAndroidID(String android_id) {
		this.android_id = android_id;
	}
}
