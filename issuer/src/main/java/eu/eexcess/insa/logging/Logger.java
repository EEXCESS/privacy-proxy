package eu.eexcess.insa.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;

public class Logger {

	protected String logDirectory = Cst.CATALINA_BASE + Config.getValue(Config.LOG_DIRECTORY);

	public Logger(){}

	public Boolean log(String interactionType, JSONObject input){
		Boolean isValidInteraction = isValidInteraction(interactionType);

		if (isValidInteraction){
			JSONObject entry = new JSONObject();

			// Generic processing:

			// Interaction type
			entry.put(Cst.TAG_INTERACTION_TYPE, interactionType);
			// Timestamp
			Date currentDate = new Date();
			Long timestamp = currentDate.getTime(); 
			entry.put(Cst.TAG_TIMESTAMP, timestamp);
			// IP address
			if (input.has(Cst.TAG_IP)){
				entry.put(Cst.TAG_IP, input.getString(Cst.TAG_IP));
			}
			// Origin
			if (input.has(Cst.TAG_ORIGIN)){
				entry.put(Cst.TAG_ORIGIN, input.getJSONObject(Cst.TAG_ORIGIN));
			} else if (input.has(Cst.TAG_CONTENT)){
				JSONObject content = input.getJSONObject(Cst.TAG_CONTENT); 
				if (content.has(Cst.TAG_USER_ID)){
					entry.put(Cst.TAG_USER_ID, content.getString(Cst.TAG_USER_ID));
				}
			}
			// Content
			if (input.has(Cst.TAG_CONTENT)){
				entry.put(Cst.TAG_CONTENT, input.getJSONObject(Cst.TAG_CONTENT));
			}

			// Interaction-specific processing:

			// Query ID
			String queryId = "";
			if (interactionType.equals(Cst.INTERACTION_QUERY)){

			} else {
				if (input.has(Cst.TAG_QUERY_ID)){
					queryId = input.getString(Cst.TAG_QUERY_ID);
				}
			}
			if (!queryId.equals("")){
				JSONObject content = entry.getJSONObject(Cst.TAG_CONTENT);
				content.put(Cst.TAG_QUERY_ID, queryId.toString());
				entry.put(Cst.TAG_CONTENT, content);
			}
			writeEntry(entry);
		}
		return isValidInteraction;
	}

	public String logQuery(JSONObject origin, String ip, JSONObject query){
		String queryId = "";
		JSONObject input = new JSONObject();
		// IP address 
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		// Content
		JSONObject content = new JSONObject();
		// Content / query
		content.put(Cst.TAG_QUERY, query);
		if (!query.has(Cst.TAG_QUERY_ID)){
			String queryIdAux = query.toString(); 
			Date currentDate = new Date();
			Long timestamp = currentDate.getTime(); 
			queryId = hash(queryIdAux, timestamp);
			// Content / queryID
			content.put(Cst.TAG_QUERY_ID, queryId);
		} else {
			content.put(Cst.TAG_QUERY_ID, query.getString(Cst.TAG_QUERY_ID));
		}
		input.put(Cst.TAG_CONTENT, content);
		log(Cst.INTERACTION_QUERY, input);
		return queryId;
	}

	public Boolean logDetailsQuery(JSONObject origin, String ip, String queryId, JSONObject detailsQuery){
		Boolean logged = false;
		JSONObject input = new JSONObject();
		// IP address 
		input.put(Cst.TAG_IP, ip);
		// Origin
		input.put(Cst.TAG_ORIGIN, origin);
		// Content
		JSONObject content = new JSONObject();
		// Content / query
		content.put(Cst.TAG_DETAILS_QUERY, detailsQuery);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		logged = log(Cst.INTERACTION_DETAILS_QUERY, input);
		return logged;
	}
	
	public Boolean logRegularResults(JSONObject origin, String ip, String queryId, JSONObject results){
		Boolean logged = false;
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		JSONObject content = new JSONObject();
		content.put(Cst.TAG_RESULTS, results);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		logged = log(Cst.INTERACTION_RESPONSE, input);
		return logged;
	}
	
	public Boolean logMergedResults(JSONObject origin, String ip, String queryId, JSONObject results){
		Boolean logged = false;
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		input.put(Cst.TAG_CONTENT, results);
		logged = log(Cst.INTERACTION_RESPONSE, input);
		return logged;
	}

	public Boolean logDetailsResults(JSONObject origin, String ip, String queryId, JSONObject results){
		Boolean logged = false;
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		JSONObject content = new JSONObject();
		content.put(Cst.TAG_RESULTS, results);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		logged = log(Cst.INTERACTION_DETAILS_RESPONSE, input);
		return logged;
	}
	
	private void writeEntry(JSONObject entry){
		File file = new File(getLogFileName());
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(entry.toString() + Cst.LINE_BREAK);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getLogFileName(){
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer year = cal.get(Calendar.YEAR);
		String yearStr = year.toString();
		Integer month = cal.get(Calendar.MONTH) + 1;
		String monthStr = month.toString();
		if (monthStr.length() < 2){
			monthStr = "0" + monthStr; 
		}
		Integer day = cal.get(Calendar.DATE);
		String dayStr = day.toString();
		if (dayStr.length() < 2){
			dayStr = "0" + dayStr; 
		}
		return this.logDirectory + yearStr + monthStr + dayStr;
	}

	private Boolean isValidInteraction(String element){
		Boolean contains = false;
		for (int i = 0 ; i < Cst.VALID_INTERACTIONS.length ; i++){
			contains = contains || (element.equals(Cst.VALID_INTERACTIONS[i]));
		}
		return contains;
	}

	private String hash(Object o1, Object o2){
		String output;
		Integer hashCode = o1.hashCode() * o2.hashCode();
		if (hashCode < 0){
			hashCode = -hashCode;
		}
		output = hashCode.toString();
		return output;
	}

}
