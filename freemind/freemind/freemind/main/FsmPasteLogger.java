package freemind.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Small logger used for the ELEC5618 Markov-chain lab.
 *
 * Each line records one FSM transition for the paste functionality. The format
 * is intentionally simple so a separate script can parse it later:
 *
 * FSM_LOG|time=...|event=...|from=...|to=...|user=...
 */
public final class FsmPasteLogger {

	private static final String LOG_FILE_NAME = "paste_fsm_log.txt";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS");

	private FsmPasteLogger() {
	}

	public static synchronized void logTransition(String event,
			String fromState, String toState) {
		String line = "FSM_LOG|time=" + DATE_FORMAT.format(new Date())
				+ "|event=" + safe(event) + "|from=" + safe(fromState)
				+ "|to=" + safe(toState) + "|user="
				+ safe(System.getProperty("user.name", "unknown"));

		System.out.println(line);
		writeToFile(line);
	}

	private static void writeToFile(String line) {
		File logFile = new File(System.getProperty("user.dir"), LOG_FILE_NAME);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(logFile, true));
			writer.println(line);
		} catch (IOException e) {
			System.err.println("FSM_LOG_WRITE_ERROR|" + e.getMessage());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static String safe(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("|", "_").replace("\r", " ").replace("\n", " ");
	}
}
