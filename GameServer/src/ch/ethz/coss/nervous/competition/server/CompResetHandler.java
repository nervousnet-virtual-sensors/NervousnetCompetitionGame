package ch.ethz.coss.nervous.competition.server;
import java.net.ServerSocket;
import java.net.Socket;

class CompResetHandler implements Runnable {

	private ServerSocket socket;
	private Competition comp;
	private CompPlayer player;
	private int contestDuration;

	public CompResetHandler(ServerSocket socket, Competition comp,
			CompPlayer player, int contestDuration) {
		this.socket = socket;
		this.comp = comp;
		this.player = player;
		this.contestDuration = contestDuration;
	}

	@Override
	public synchronized void run() {
		Socket clientSocket = null;

		try {

			while (true) {

				comp.reset();
				player.pause();

				clientSocket = socket.accept();
				WriteJSON.sendJSON(clientSocket, (int) contestDuration);
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
}