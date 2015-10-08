package ch.ethz.coss.nervous.competition.server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ch.ethz.coss.nervous.competition.model.AccReading;
import ch.ethz.coss.nervous.competition.model.LightReading;
import ch.ethz.coss.nervous.competition.model.NoiseReading;
import ch.ethz.coss.nervous.competition.model.Reading;

class CompetitionDAO {

	String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	String mkString(Object... objects) {
		if (objects == null || objects.length == 0)
			return "";
		String s = "";
		for (Object o : objects)
			s += o + " ";
		return s.substring(0, s.length() - 1);
	}

	public void store(Competition comp) {
		List<Reading> readings = comp.getReadings();
		// System.out.println(getDate());
		File f = new File(getDate());

		PrintWriter out = null;

		try {

			f.createNewFile();

			out = new PrintWriter(new FileOutputStream(f));

			for (Reading r : readings) {

				if (r instanceof AccReading) {
					AccReading ar = (AccReading) r;
					out.println(mkString(ar.timestamp, ar.team, ar.x, ar.y,
							ar.z));
				} else {
					LightReading ar = (LightReading) r;
					out.println(mkString(ar.timestamp, ar.team, ar.lightVal));
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			f.delete();
		} finally {
			out.close();
		}
	}
}
