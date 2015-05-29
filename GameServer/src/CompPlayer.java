public class CompPlayer {

	LoopPlayer[] player = new LoopPlayer[2];

	CompPlayer(String songA, String songB) {
		player[0] = new LoopPlayer(songA);
		player[1] = new LoopPlayer(songB);
	}

	public void pause() {
		player[0].pause();
		player[1].pause();
	}

	class Pauser implements Runnable {
		int seconds;
		LoopPlayer player;

		Pauser(LoopPlayer player, int seconds) {
			this.seconds = seconds;
			this.player = player;
		}

		public void run() {
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				player.pause();
			}
		}
	}

	public void play(int team, int seconds) {

		Pauser pauser = new Pauser(player[team], seconds);

		player[team].play();

		new Thread(pauser).start();
	}
}
