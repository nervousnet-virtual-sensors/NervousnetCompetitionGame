package com.example.old;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class OldSynchWriter {

	ObjectOutputStream oos;
	Object monitor = new Object();
	String ipAddress;
	int port;
	int writingInterval;
	Timer timer;

	public OldSynchWriter(String ipAddress, int port, int writingInterval)
			throws IOException {
		this.ipAddress = ipAddress;
		this.port = port;
		this.oos = createObjectOutputStream();
		this.writingInterval = writingInterval;

		timer = new Timer();
		timer.schedule(new FlushTask(), writingInterval, writingInterval);
	}

	public void send(Object o) {
		new Thread(new OutputTask(o)).start();
	}

	private ObjectOutputStream createObjectOutputStream() {

		if (oos == null) {
			try {
				@SuppressWarnings("resource")
				Socket socket = new Socket(ipAddress, port);
				OutputStream os = new BufferedOutputStream(
						socket.getOutputStream());
				oos = new ObjectOutputStream(os);
				return oos;
			} catch (Exception e) {

				e.printStackTrace();
				return null;
			}
		}

		return oos;
	}

	class OutputTask implements Runnable {
		Object o;

		public OutputTask(Object o) {
			this.o = o;
		}

		@Override
		public void run() {
			synchronized (monitor) {
				try {
					if (oos == null) {
						oos = createObjectOutputStream();
					}
					if (oos != null) {
						oos.writeObject(o);
					}
				} catch (Exception e) {
					e.printStackTrace();
					oos = null;
				}
			}
		}
	}

	class FlushTask extends TimerTask {
		@Override
		public void run() {
			synchronized (monitor) {
				try {
					if (oos == null) {
						oos = createObjectOutputStream();
					}
					if (oos != null) {
						oos.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
					oos = null;
				}
			}
		}
	}

	public void cancelFlushing() {
		timer.cancel();
	}
}
