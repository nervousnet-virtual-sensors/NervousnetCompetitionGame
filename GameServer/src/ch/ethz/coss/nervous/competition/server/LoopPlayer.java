package ch.ethz.coss.nervous.competition.server;
import maryb.player.Player;
import maryb.player.PlayerEventListener;

class LoopPlayer {

	Player player;

	public LoopPlayer(String song) {
		player = new Player();
		player.setSourceLocation(song);
	}

	public void play() {

		player.setListener(new PlayerEventListener() {

			@Override
			public void endOfMedia() {
				player.stop();
				player.play();
			}

			@Override
			public void buffer() {
			}

			@Override
			public void stateChanged() {
			}
		});

		player.play();
	}

	public void pause() {
		player.pause();
	}

	public static void main(String[] args) throws Exception {

		LoopPlayer player = new LoopPlayer("sample.mp3");

		player.play();
	}
}