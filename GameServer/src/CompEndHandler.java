import java.net.ServerSocket;
import java.net.Socket;

class CompEndHandler implements Runnable {
	
	private ServerSocket socket;
	private Competition comp;
	private CompPlayer player;
	private int playsecs;
	private CompetitionDAO dao;
	
	public CompEndHandler(ServerSocket socket, Competition comp, CompPlayer player, CompetitionDAO dao, int playsecs) {
		this.socket = socket;
		this.comp = comp;
		this.player = player;
		this.dao = dao;
		this.playsecs = playsecs;
	} 		
	
	@Override
	public synchronized void run() {						
		Socket clientSocket = null;
		
		try {				
						
			while(true) {
								
				clientSocket = socket.accept();
				WriteJSON.sendJSON(clientSocket,0);				
				if(comp.getScore() < 0.5) {
					player.play(0,playsecs);					
				} else {				
					player.play(1,playsecs);
				}				
				dao.store(comp);
			}
		} catch (Exception e) {				
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}		
	
	static class Data {
		int value,secs;
		Data(int value, int secs) {
			this.value = value;
			this.secs = secs;
		}
	}
}