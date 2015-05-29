package com.example.nervousco;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SynchWriter {

	ArrayList<Object> data = new ArrayList<Object>();
	OutputTask outTask = new OutputTask();
	String ipAddress;
	int port;
	int writingInterval;
	boolean printTrace = false;
	int sensorType;

	Timer timer;

	public SynchWriter(String ipAddress, int port, int writingInterval)
			throws IOException {
		this.port = port;
		this.writingInterval = writingInterval;
		this.ipAddress = ipAddress;
		timer = new Timer();
		timer.schedule(outTask, writingInterval, writingInterval);
	}

	public void send(Object o) {
		synchronized (data) {
			data.add(o);
		}
	}

	class OutputTask extends TimerTask {

		ObjectOutputStream oos;

		@Override
		@SuppressWarnings("unchecked")
		public synchronized void run() {

			ArrayList<Object> buffer = null;

			synchronized (data) {
				buffer = (ArrayList<Object>) data.clone();
				data.clear();
			}

			ObjectOutputStream out = getObjectOutputStream();

			if (out == null)
				return;

			try {
				for (Object o : buffer) {
					out.writeObject(o);
				    System.out.println("Writing data on time: "+System.currentTimeMillis());
				}
				out.flush();
			} catch (Exception e) {
				if (printTrace)
					e.printStackTrace();
				try {
					oos.close();
				} catch (Exception f) {
					if (printTrace)
						f.printStackTrace();
				} finally {
					oos = null;
				}
			}
		}

		private synchronized ObjectOutputStream getObjectOutputStream() {

			if (oos == null) {
				try {
					System.out.println("Before Writing to server at "
							+ ipAddress + ":" + port);
					@SuppressWarnings("resource")
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ipAddress, port), 1000);
					// System.out.println("Writing to server");
					OutputStream os = new BufferedOutputStream(
							socket.getOutputStream());
					oos = new ObjectOutputStream(os);
				} catch (Exception e) {
					System.out.println("Exception thrown here "
							+ e.getMessage());
					if (printTrace)
						e.printStackTrace();
					try {
						if (oos != null) {
							oos.close();
						}
					} catch (Exception f) {
						if (printTrace)
							f.printStackTrace();
					} finally {
						oos = null;
					}
				}
			} else {
				// System.out.println("oos is null");
			}
			return oos;
		}

		synchronized void stop() {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				if (printTrace)
					e.printStackTrace();
			}
		}
	}

	public void stop() {
		timer.cancel();
		outTask.stop();
	}

	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}

	public void setServerAddress(String address) {
		this.ipAddress = address;
	}

	public void setServerPort(int port) {
		this.port = port;
	}

	public void setWritingInterval(int writingInterval) {
		this.writingInterval = writingInterval;
	}
}
