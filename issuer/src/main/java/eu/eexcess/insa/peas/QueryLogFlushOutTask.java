package eu.eexcess.insa.peas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import eu.eexcess.Config;
import eu.eexcess.Cst;

/**
 * This class is used to flush out the query log. 
 * The goal is to prevent it from becoming too voluminous and unexploitable. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class QueryLogFlushOutTask extends TimerTask {
	
	protected String queryLogLocation = Cst.CATALINA_BASE + Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	protected String tmpQueryLogLocation = Cst.CATALINA_BASE + Config.getValue(Config.DATA_DIRECTORY) + Cst.TMP_FILE_PREFIX + Config.getValue(Config.QUERY_LOG);
	protected Long window = Long.valueOf(Config.getValue(Config.QUERY_LOG_WINDOW));

	/**
	 * Flushes out the query log and keeps only the queries sent in the time window (. 
	 * This method does not have to be called explicitly, as a scheduler is supposed to do it. 
	 */
	@Override
	public void run() {
		File log = new File(queryLogLocation);
		File tmpLog = new File(tmpQueryLogLocation);
		Long now = new Date().getTime();
		Integer cntKept = 0;
		Integer cntDeleted = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(log));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tmpLog));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] arrayLine = currentLine.split(Cst.COLUMN_SEPARATOR);
				Long timestamp = Long.valueOf(arrayLine[0]);
				Long limit = now - window;
				if (timestamp.compareTo(limit) > 0){
					writer.write(currentLine + Cst.LINE_BREAK);
					cntKept++;
				} else {
					cntDeleted++;
				}
			}
			writer.close(); 
			reader.close(); 
			tmpLog.renameTo(log);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
