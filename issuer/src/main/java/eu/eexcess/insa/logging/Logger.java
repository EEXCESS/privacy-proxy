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

/**
 * This class defines all the methods needed to log user interactions on the Privacy Proxy. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class Logger {

	private String logDirectoryName = Cst.CATALINA_BASE + Config.getValue(Config.LOG_DIRECTORY);

	private static volatile Logger instance = null;
	
	/** Default constructor. */
	private Logger(){
		File logDirectory = new File(logDirectoryName);
		if (!logDirectory.exists()){
			logDirectory.mkdirs();
		}
	}
	
	/**
	 * Method used in the implementation of the Singleton pattern. 
	 * @return an instance of {@code Logger}. 
	 */
	public static Logger getInstance(){
		if (instance == null){
			instance = new Logger();
		}
		return instance;
	}
	
	/**
	 * Logs generic interactions. 
	 * @param interactionType Type of the interaction. 
	 * @param input Data to be logged. 
	 * @return {@code true} if the input was logged; {@code false} otherwise. 
	 */
	public Boolean log(String interactionType, JSONObject input) {
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
				if (input.has(Cst.TAG_QUERY)){
					JSONObject query = input.getJSONObject(Cst.TAG_QUERY);
					if (query.has(Cst.TAG_QUERY_ID)){
						queryId = query.getString(Cst.TAG_QUERY_ID);
					}
				}
			} else if (input.has(Cst.TAG_QUERY_ID)){
				queryId = input.getString(Cst.TAG_QUERY_ID);
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

	/**
	 * Logs a recommendation interaction. 
	 * @param origin Origin of the interaction. 
	 * @param ip IP address of the user. 
	 * @param queryId Identifier of the query. 
	 * @param query JSON object representing the query. 
	 * @return {@code true} if the input was logged; {@code false} otherwise. 
	 */
	public Boolean logQuery(JSONObject origin, String ip, String queryId, JSONObject query){
		JSONObject input = new JSONObject();
		// IP address 
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		// Content
		JSONObject content = new JSONObject();
		// Content / query
		content.put(Cst.TAG_QUERY, query);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		return log(Cst.INTERACTION_QUERY, input);
	}

	/**
	 * Logs a getDetails interaction. 
	 * @param origin Origin of the interaction. 
	 * @param ip IP address of the user. 
	 * @param queryId Identifier of the query. 
	 * @param detailsQuery JSON object representing the details query.
	 * @return {@code true} if the input was logged; {@code false} otherwise.
	 */
	public Boolean logDetailsQuery(JSONObject origin, String ip, String queryId, JSONObject detailsQuery){
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
		return log(Cst.INTERACTION_DETAILS_QUERY, input);
	}
	
	/**
	 * Logs the results of a regular query interaction. 
	 * @param origin Origin of the interaction. 
	 * @param ip IP address of the user. 
	 * @param queryId Identifier of the query. 
	 * @param results JSON object representing the results corresponding to a query. 
	 * @return {@code true} if the input was logged; {@code false} otherwise.
	 */
	public Boolean logRegularResults(JSONObject origin, String ip, String queryId, JSONObject results){
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		JSONObject content = new JSONObject();
		content.put(Cst.TAG_RESULTS, results);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		return log(Cst.INTERACTION_RESPONSE, input);
	}
	
	/**
	 * Logs the results of an obfuscated query interaction. 
	 * @param origin Origin of the interaction. 
	 * @param ip IP address of the user. 
	 * @param queryId Identifier of the query. 
	 * @param results JSON object representing the results corresponding to a query. 
	 * @return {@code true} if the input was logged; {@code false} otherwise.
	 */
	public Boolean logMergedResults(JSONObject origin, String ip, String queryId, JSONObject results){
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		input.put(Cst.TAG_CONTENT, results);
		return log(Cst.INTERACTION_RESPONSE, input);
	}

	/**
	 * Logs the results of a details query interaction. 
	 * @param origin Origin of the interaction. 
	 * @param ip IP address of the user. 
	 * @param queryId Identifier of the query. 
	 * @param results JSON object representing the results corresponding to a query. 
	 * @return {@code true} if the input was logged; {@code false} otherwise.
	 */
	public Boolean logDetailsResults(JSONObject origin, String ip, String queryId, JSONObject results){
		JSONObject input = new JSONObject();
		input.put(Cst.TAG_IP, ip);
		input.put(Cst.TAG_ORIGIN, origin);
		JSONObject content = new JSONObject();
		content.put(Cst.TAG_RESULTS, results);
		content.put(Cst.TAG_QUERY_ID, queryId);
		input.put(Cst.TAG_CONTENT, content);
		return log(Cst.INTERACTION_DETAILS_RESPONSE, input);
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
		return this.logDirectoryName + yearStr + monthStr + dayStr;
	}

	private Boolean isValidInteraction(String element){
		Boolean contains = false;
		for (int i = 0 ; i < Cst.VALID_INTERACTIONS.length ; i++){
			contains = contains || (element.equals(Cst.VALID_INTERACTIONS[i]));
		}
		return contains;
	}

	/**
	 * Determines if an interaction must be logged based on the JSON input. 
	 * @param input A JSON object. 
	 * @return {@code true} is the interaction must be logged; {@code false} otherwise. 
	 */
	public static Boolean mustBeLogged(JSONObject input){
		Boolean mustBeLogged = true;
		if (input.has(Cst.TAG_LOGGING_LEVEL)){
			mustBeLogged = (input.getInt(Cst.TAG_LOGGING_LEVEL) == 0);
			input.remove(Cst.TAG_LOGGING_LEVEL);
		}
		return mustBeLogged;
	}
	
}
