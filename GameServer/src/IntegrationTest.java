import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.example.nervousco.AccReading;

public class IntegrationTest {
	
	public static String ip = "127.0.0.1";
	public static int numberAndroids = 2;	
	
	public static void main(String[] args) throws Exception {
				
//		Socket resetSocket = new Socket(ip,Main.compResetPort);		
//		PrintWriter out = new PrintWriter(resetSocket.getOutputStream());
//		
//		out.println("start/reset competition");
//		out.println();
//		out.flush();
//		resetSocket.close();
		
		for(int i = 0; i < numberAndroids; i++) {
			new Thread(new MockSmartphoneClient(Main.smartphonesPort)).start();
		}
			
//		new Thread(new MockJsClient(Main.javascriptPort)).start();	
	}	
	
	static class MockJsClient implements Runnable {
		
		int port;		
		
		MockJsClient(int port) {
			this.port = port;	
		}

		@Override
		public void run() {
			try {				
				
				while(true) {
					Thread.sleep((int)(100 * Math.random()));					
					
					Socket socket = new Socket(ip,port);										
					OutputStream os = new BufferedOutputStream(socket.getOutputStream());					
					PrintWriter out = new PrintWriter(os);																				
					Scanner in = new Scanner(socket.getInputStream());
					
					out.println("http json request");
					out.println();
					out.flush();					
										
					System.out.println("num_messages: "+in.nextInt());
					
					socket.close();					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	static class MockSmartphoneClient implements Runnable {
		
		ObjectOutputStream oos;
		
		MockSmartphoneClient(int port) {
			try {
				Socket socket = new Socket(ip,port);
				OutputStream os = new BufferedOutputStream(socket.getOutputStream());					
				oos = new ObjectOutputStream(os);								
			} catch (Exception e) {				
				e.printStackTrace();
				throw new RuntimeException(e);
			} 		
		}

		@Override
		public void run() {
						
			try {			
				
				while(true) {										
					Thread.sleep((int)(10 * Math.random()));																								
						
					AccReading reading = new AccReading(
						0.0,0.0,0.0,System.nanoTime(),Math.random() <= 1? 1:1);
										
					oos.writeObject(reading);
					oos.flush();					
				}					
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
