import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.nervousco.AccReading;
import com.example.nervousco.LightReading;
import com.example.nervousco.Reading;

class AccReadingListener implements Runnable {

	ServerSocket socket;
	Competition comp;

	public AccReadingListener(ServerSocket socket, Competition comp) {
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
				AccReadingHandler h = new AccReadingHandler(clientSocket, comp);
				new Thread(h).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class AccReadingHandler implements Runnable {
		double totDelay = 0;
		int nReqs = 0;
		Socket socket;
		Competition comp;

		public AccReadingHandler(Socket socket, Competition comp) {
			this.socket = socket;
			this.comp = comp;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream(
						new BufferedInputStream(socket.getInputStream()));
				while (true) {
					// System.out.println("Before reading object");
					Reading reading = (Reading) in.readObject();
					if (reading instanceof AccReading) {
						// AccReading accReading= (AccReading)in.readObject();
						// System.out.println("VaLues coming from Client ****"+(((AccReading)reading).team
						// == 0?
						// "GREEN":"RED")+"****, °°°°ACCELEROMETER°°°°: "+(AccReading)reading);
						comp.pushReading((AccReading) reading);

						double delay = (double) (System.currentTimeMillis() - ((AccReading) reading).timestamp);
						totDelay += delay;
						nReqs++;
					} else {
						// LightReading lightReading =
						// (LightReading)in.readObject();
						// System.out.println("VaLues coming from Client ****"+(((LightReading)reading).team
						// == 0?
						// "GREEN":"RED")+"****, °°°°LIGHT°°°° : "+(LightReading)reading);
						comp.pushReading((LightReading) reading);

						double delay = (double) (System.currentTimeMillis() - ((LightReading) reading).timestamp);
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
