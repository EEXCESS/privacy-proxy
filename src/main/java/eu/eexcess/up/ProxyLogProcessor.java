package eu.eexcess.up;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Cst;
import eu.eexcess.Util;

public class ProxyLogProcessor {

	/**
	 * 
	 * @param interactionType
	 * @param origin
	 * @param ip
	 * @param request
	 */
	public void process(String interactionType, String origin, String ip, String request) {
		process(interactionType, origin, ip, request, null);
	}

	/**
	 * 
	 * @param interactionType
	 * @param origin
	 * @param ip
	 * @param request
	 * @param answer
	 */
	public void process(String interactionType, String origin, String ip, String request, String answer) {

		String userID = "";
		String msg; 

		if (interactionType.equals(Cst.RESULT)) {

			JSONObject jsonOutput = new JSONObject(); // Will look like: {"results":<resultArr>], "query":q}
			JSONObject jsonInputRequest = new JSONObject(request);
			JSONObject jsonInputAnswer = new JSONObject(answer);
			if (jsonInputRequest.has(Cst.TAG_UUID)){
				userID = jsonInputRequest.getString(Cst.TAG_UUID);
			}
			if (jsonInputAnswer.has(Cst.TAG_RESULT)){

				JSONArray jsonOutputResults = new JSONArray(); // Will look like: [{"p":a,"id":b}, {"p":c,"id":d}, ..., {"p":y,"id":z}]
				JSONArray jsonInputResults = jsonInputAnswer.getJSONArray(Cst.TAG_RESULT);
				for (Integer i = 0 ; i < jsonInputResults.length() ; i++){
					JSONObject jsonInputEntry = jsonInputResults.getJSONObject(i);
					JSONObject jsonOutputEntry = new JSONObject();
					jsonOutputEntry.put(Cst.TAG_PROVIDER_SHORT, jsonInputEntry.getJSONObject(Cst.TAG_FACETS).get(Cst.TAG_PROVIDER));
					jsonOutputEntry.put(Cst.TAG_ID, jsonInputEntry.get(Cst.TAG_ID));
					jsonOutputResults.put(jsonOutputEntry);
				}

				jsonOutput.put(Cst.TAG_RESULTS, jsonOutputResults);
				if (jsonInputRequest.has(Cst.TAG_QUERY_ID)){
					jsonOutput.put(Cst.TAG_QUERY_ID, jsonInputRequest.getString(Cst.TAG_QUERY_ID));
				}
			}

			msg = Util.sBrackets(interactionType) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_USER_ID, userID) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
					+ jsonOutput.toString();
			Cst.LOGGER_INTERACTION.trace(msg);
		} else {
			msg = Util.sBrackets(interactionType) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_USER_ID, userID) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
					+ request;
			Cst.LOGGER_INTERACTION.trace(msg);
		}

	}
}