import java.net.ServerSocket;
import java.net.Socket;

class JsRequestHandler implements Runnable {

	private ServerSocket socket;
	private Competition comp;

	public JsRequestHandler(ServerSocket socket, Competition comp) {
		this.socket = socket;
		this.comp = comp;
	}

	@Override
	public synchronized void run() {
		Socket clientSocket = null;

		try {

			while (true) {

				clientSocket = socket.accept();

				int value = (int) Math.round(comp.getScore() * 100);
				if (value == 50) {
					value = 49;
				}

				WriteJSON.sendJSON(clientSocket, value, comp.getLastReadings(), comp.COMPETITION_TYPE);
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