package ch.ethz.coss.nervous.competition.server;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class JSServerDetailsRequestHandler implements Runnable {

	private ServerSocket socket;
	private Competition comp;

	public JSServerDetailsRequestHandler(ServerSocket socket, Competition comp) {
		this.socket = socket;
		this.comp = comp;
	}

	@Override
	public synchronized void run() {
		Socket clientSocket = null;

		try {

			while (true) {

				clientSocket = socket.accept();

				InetAddress IP = InetAddress.getLocalHost();
				String serverIP = IP.getHostAddress();
				WriteJSON.sendJSON(clientSocket, serverIP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static class Data {
		int value, secs;

		Data(int value, int secs) {
			this.value = value;
			this.secs = secs;
		}
	}
}