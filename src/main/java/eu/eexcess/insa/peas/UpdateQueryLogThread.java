package eu.eexcess.insa.peas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Cst;

public class UpdateQueryLogThread extends Thread {
	
	protected static final String LINE_BREAK = "\n"; 
	
	protected String queryLogLocation;
	protected JSONObject query;
	
	
	/**
	 * 
	 * @param queryLogLocation
	 * @param query Query of format QF1. 
	 */
	public UpdateQueryLogThread(String queryLogLocation, JSONObject query){
		this.queryLogLocation = queryLogLocation;
		this.query = query;
	}
	
	public void run() {
		Date currentDate = new Date();
		Long timestamp = currentDate.getTime();
		String queryString = "";
		if (query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			JSONArray queryArray = query.getJSONArray(Cst.TAG_CONTEXT_KEYWORDS);
			for (int i = 0 ; i < queryArray.length() ; i++){
				JSONObject queryTerm = queryArray.getJSONObject(i);
				if (queryTerm.has(Cst.TAG_QUERY_TEXT)){
					queryString += queryTerm.getString(Cst.TAG_QUERY_TEXT);
					if (i < (queryArray.length() - 1)){
						queryString += Cst.KEYWORDS_SEPARATOR;
					}
				}
				
			}
		}
		try {
			FileWriter writer = new FileWriter(queryLogLocation, true);
			BufferedWriter buffer = new BufferedWriter(writer);
			PrintWriter print = new PrintWriter(buffer);
			print.write(timestamp + Cst.COLUMN_SEPARATOR + queryString + LINE_BREAK);
			print.close();
			buffer.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
