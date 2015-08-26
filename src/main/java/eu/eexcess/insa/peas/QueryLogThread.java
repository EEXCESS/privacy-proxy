package eu.eexcess.insa.peas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;

/**
 * This class is used to add a query in the log without interrupting the processing of the query. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class QueryLogThread extends Thread {
	
	protected static String queryLogLocation = Cst.CATALINA_BASE + Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	protected JSONObject query;
	
	/**
	 * Default constructor. 
	 */
	public QueryLogThread(){}
	
	/**
	 * Triggers the logging of a query. 
	 * @param query Query to be logged. 
	 */
	public void log(JSONObject query) {
		this.query = query;	
		super.start();
	}
	
	/**
	 * Logs the query (format QF1) in the file. 
	 * The query is added at the end of the log and is associated with a time stamp. 
	 */
	@Override
	public void run() {
		if (query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			Date currentDate = new Date();
			String queryString = currentDate.getTime() + Cst.COLUMN_SEPARATOR;
			JSONArray queryArray = query.getJSONArray(Cst.TAG_CONTEXT_KEYWORDS);
			for (int i = 0 ; i < queryArray.length() ; i++){
				JSONObject queryTerm =  queryArray.getJSONObject(i);
				if (queryTerm.has(Cst.TAG_QUERY_TEXT)){
					queryString += queryTerm.getString(Cst.TAG_QUERY_TEXT);
					if (i < (queryArray.length() - 1)){
						queryString += Cst.KEYWORDS_SEPARATOR;
					}
				}
				
			}
			queryString += Cst.LINE_BREAK;
			try {
				FileWriter writer = new FileWriter(queryLogLocation, true);
				BufferedWriter buffer = new BufferedWriter(writer);
				PrintWriter print = new PrintWriter(buffer);
				print.write(queryString);
				print.close();
				buffer.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
