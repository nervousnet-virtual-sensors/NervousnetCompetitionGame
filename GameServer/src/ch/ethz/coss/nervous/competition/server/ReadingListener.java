package ch.ethz.coss.nervous.competition.server;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ch.ethz.coss.nervous.competition.model.AccReading;
import ch.ethz.coss.nervous.competition.model.LightReading;
import ch.ethz.coss.nervous.competition.model.NoiseReading;
import ch.ethz.coss.nervous.competition.model.Reading;

class ReadingListener implements Runnable {

	ServerSocket socket;
	Competition comp;

	public ReadingListener(ServerSocket socket, Competition comp) {
		this.socket = socket;
		this.comp = comp;
	}

	@Override
	public synchronized void run() {
		while (true) {
			try {

				// WifiManager wm = (WifiManager)
				// getSystemService(WIFI_SERVICE);
				// String ip =
				// Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

				System.out
						.println("*************Server Started****************");
				// Enumeration en = NetworkInterface.getNetworkInterfaces();
				// while(en.hasMoreElements()){
				// NetworkInterface ni=(NetworkInterface) en.nextElement();
				// Enumeration ee = ni.getInetAddresses();
				// while(ee.hasMoreElements()) {
				// InetAddress ia= (InetAddress) ee.nextElement();
				// System.out.println(ia.getHostAddress());
				// }
				// }
				InetAddress IP = InetAddress.getLocalHost();
				System.out.println("IP of Server is := " + IP.getHostAddress());
				System.out.println("Server getInetAddress- "
						+ socket.getInetAddress());
				System.out.println("Server LocalSocketAddress- "
						+ socket.getLocalSocketAddress());
				System.out
						.println("Server LocalPort- " + socket.getLocalPort());
				System.out
						.println("*******************************************");
				Socket clientSocket = socket.accept();
				ReadingHandler h = new ReadingHandler(clientSocket, comp);
				new Thread(h).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class ReadingHandler implements Runnable {
		double totDelay = 0;
		int nReqs = 0;
		Socket socket;
		Competition comp;

		public ReadingHandler(Socket socket, Competition comp) {
			this.socket = socket;
			this.comp = comp;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(
						new BufferedInputStream(socket.getInputStream()));
				while (true) {
					Reading reading = (Reading) in.readObject();
					if (reading instanceof AccReading) { 
						if(comp.COMPETITION_TYPE != Competition.COMPETITION_TYPE_ACCELEROMETER){
							comp.reset();
							comp.COMPETITION_TYPE = Competition.COMPETITION_TYPE_ACCELEROMETER;
						}
						comp.pushReading((AccReading) reading);
						double delay = (double) (System.currentTimeMillis() - reading.timestamp);
						totDelay += delay;
						nReqs++;
					} else if (reading instanceof LightReading){
						if(comp.COMPETITION_TYPE != Competition.COMPETITION_TYPE_LIGHT){
							comp.reset();
							comp.COMPETITION_TYPE = Competition.COMPETITION_TYPE_LIGHT;
						}
						comp.pushReading((LightReading) reading);

						double delay = (double) (System.currentTimeMillis() - ((LightReading) reading).timestamp);
						totDelay += delay;
						nReqs++;
					}else if (reading instanceof NoiseReading){
						if(comp.COMPETITION_TYPE != Competition.COMPETITION_TYPE_NOISE){
							comp.reset();
							comp.COMPETITION_TYPE = Competition.COMPETITION_TYPE_NOISE;
						}
						comp.pushReading((NoiseReading) reading);
						double delay = (double) (System.currentTimeMillis() - ((NoiseReading) reading).timestamp);
						totDelay += delay;
						nReqs++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
