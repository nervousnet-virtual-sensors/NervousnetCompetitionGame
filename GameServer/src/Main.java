import java.net.InetAddress;
import java.net.ServerSocket;

public class Main {
	
	public static String songA = "eye.wav";
	public static String songB = "life.wav";
	public static int playDuration = 1;
	
	public static int compResetPort = 6060;
	public static int javascriptServerDetailsPort = 7272;	
	public static int javascriptPortAcc = 7070;	
	public static int javascriptPortLight = 7171;	
	public static int smartphonesPort = 8080;
	public static int compEndPort = 9090;
	public static int contestDuration = 30;
	
	public static void main(String[] args) throws Exception {									    			
		
		ServerSocket javascriptAppAcc = null, javascriptAppLight = null, javascriptServerDetails = null, androidApp = null, 
						compReset = null, compEnd = null;
										
		try {
			javascriptServerDetails =  new ServerSocket(javascriptServerDetailsPort);
			javascriptAppAcc = new ServerSocket(javascriptPortAcc);
			javascriptAppLight = new ServerSocket(javascriptPortLight);
			androidApp = new ServerSocket(smartphonesPort);
			compReset = new ServerSocket(compResetPort);
			compEnd = new ServerSocket(compEndPort);
		
			CompPlayer player = new CompPlayer(songA,songB);	
			Competition comp = new Competition();	
			CompetitionDAO dao = new CompetitionDAO();
			
			AccReadingListener accReadingListener = new AccReadingListener(androidApp,comp);			
			new Thread(accReadingListener).start();
			
			JSServerDetailsRequestHandler jsServerDetailsRequestHandlerAcc = new JSServerDetailsRequestHandler(javascriptServerDetails,comp);			
			new Thread(jsServerDetailsRequestHandlerAcc).start();
			
			JsRequestHandler jsRequestHandlerAcc = new JsRequestHandler(javascriptAppAcc,comp);			
			new Thread(jsRequestHandlerAcc).start();
			
			JsRequestHandler jsRequestHandlerLight = new JsRequestHandler(javascriptAppLight,comp);			
			new Thread(jsRequestHandlerLight).start();
			
			
			CompEndHandler compEndHandler = new CompEndHandler(compEnd,comp,player,dao,playDuration);			
			new Thread(compEndHandler).start();
			
			CompResetHandler compResetHandler = new CompResetHandler(compReset,comp,player,contestDuration);			
			new Thread(compResetHandler).start();
									
		} catch(Exception e) {	
			e.printStackTrace();	
			javascriptServerDetails.close();
			javascriptAppAcc.close();
			javascriptAppLight.close();
			androidApp.close();			
			return;
		}		
	}			
}