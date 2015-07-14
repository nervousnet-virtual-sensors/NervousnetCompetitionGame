package ch.ethz.soms.nervous.competition;

import android.content.Context;
import android.telephony.TelephonyManager;

public class UniqueIDHandler {

	private static String UUID = null;
	private static final String fileName = "UNIQUE_ID";
	
	public UniqueIDHandler() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static String getIMEI(Context context){

	    TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); 
	    String imei = mngr.getDeviceId();
	    return imei;

	}

}
