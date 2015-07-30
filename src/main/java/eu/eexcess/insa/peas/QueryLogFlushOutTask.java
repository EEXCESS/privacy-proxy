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

public class QueryLogFlushOutTask extends TimerTask {

	private final String tempFilePrefix = "tmp-";
	
	protected String queryLogLocation = Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	protected String tempQueryLogLocation = Config.getValue(Config.DATA_DIRECTORY) + tempFilePrefix + Config.getValue(Config.QUERY_LOG);
	protected Long window = Long.valueOf(Config.getValue(Config.QUERY_LOG_WINDOW));

	@Override
	public void run() {
		File log = new File(queryLogLocation);
		File tmpLog = new File(tempQueryLogLocation);
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
		System.out.println("Query log flushed out [kept: " + cntKept + ", deleted: " + cntDeleted + "]");// XXX Remove this line
	}

}
