package eu.eexcess.insa.peas;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.Cst;
import eu.eexcess.insa.QueryFormats;

/**
 * Three formats of queries are handled in this component (see {@link eu.eexcess.insa.QueryFormats}): QF1, QF2 and QF3.  
 * Three formats of results are handled in this component: 
 * <ul>
 * 	<li>RF1: represents a result of the form r = [r1, r2, r3]. This format is the one returned by the federated recommender.</li>
 *  <li>RF2: represents a result of the form r = [[r1, r2, r3], [r4, r5, r6], [r7, r8, r9]]. </li>
 *  <li>RF3: represents a result of the form r = [R1, R2, R3]. Each R is the detailed information of a resoruce. </li>
 * </ul>
 * Queries of format QF1, QF2 and QF3 respectively return a result of format RF1, RF2 and RF3. 
 * @author Thomas Cerqueus
 * @version 2.0
 * @see eu.eexcess.insa.QueryFormats
 */
public class QueryEngine {

	private static volatile QueryEngine instance = null;
	
	/** Default constructor. */
	private QueryEngine(){}
	
	/**
	 * Method used in the implementation of the Singleton pattern. 
	 * @return an instance of {@code QueryLog}. 
	 */
	public static QueryEngine getInstance(){
		if (instance == null){
			instance = new QueryEngine();
		}
		return instance;
	}

	/**
	 * Alters the query to generate a query identifier (if needed), 
	 * to remove the user identifier, and to remove the origin. 
	 * @param origin Origin of the query. 
	 * @param query Query of format QF1 or QF2. 
	 * @return A query (QF1 or QF2). 
	 */
	public JSONObject alterQuery(String origin, JSONObject query){
		// Generates a query ID if it does not exist yet
		if (!query.has(Cst.TAG_QUERY_ID) && query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			String queryHash = String.valueOf(query.get(Cst.TAG_CONTEXT_KEYWORDS).toString().hashCode());
			queryHash += System.currentTimeMillis();
			query.put(Cst.TAG_QUERY_ID, queryHash);
		}
		// Removes the user ID if it exists
		if (query.has(Cst.TAG_UUID)){ query.remove(Cst.TAG_UUID); }
		// Removes the origin if it exists
		if (query.has(Cst.TAG_ORIGIN)){ query.remove(Cst.TAG_ORIGIN); }
		return query;
	}

	/**
	 * Determines if the query is obfuscated (format QF2) or not (QF1). 
	 * @param query A query. 
	 * @return {@code true} if the format of {@code query} is QF1; {@code false} otherwise. 
	 */
	public Boolean isObfuscatedQuery(JSONObject query){
		Boolean isObfuscated = false;
		if (query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			JSONArray queryArray = query.getJSONArray(Cst.TAG_CONTEXT_KEYWORDS);
			if (queryArray.length() > 0){
				isObfuscated = (queryArray.get(0) instanceof org.json.JSONArray); // It can be done, as there is at least 1 element in the array.
			}
		}
		return isObfuscated;
	}

	/**
	 * Processes a query of format QF1, QF2 or QF3.  
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param query Any type of query: QF1, QF2, or details query. 
	 * @param type Type of the query to be processed
	 * @return Result of format RF1, RF2 or RF3 (depending on the format of the query). 
	 */
	public Response processQuery(String origin, HttpServletRequest req, JSONObject query, QueryFormats type){
		Response resp = Response.status(Status.BAD_REQUEST).build();
		if (type.equals(QueryFormats.QF1)) {
			QueryLogThread thread = new QueryLogThread();
			thread.log(query);
		} 
		if (type.equals(QueryFormats.QF2)){
			resp = processObfuscatedQuery(origin, req, query);
		} else if (type.equals(QueryFormats.QF1) || type.equals(QueryFormats.QF3)){
			String serviceUrl = Cst.SERVICE_RECOMMEND;
			if (type.equals(QueryFormats.QF3)){
				serviceUrl = Cst.SERVICE_GET_DETAILS;
			} 
			Client client = Client.create();
			WebResource webResource = client.resource(serviceUrl);
			ClientResponse response = webResource
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, query.toString());
			String output = response.getEntity(String.class);
			if (type.equals(QueryFormats.QF3)){
				// Not sure why it's needed on the privacy proxy
				output = correctDetailField(output);
			}
			Integer status = response.getStatus();

			if (status.equals(Response.Status.OK.getStatusCode())){
				resp = Response.ok().entity(output).build();
			} else if (status.equals(Response.Status.CREATED.getStatusCode())){
				resp = Response.status(response.getStatus()).entity(output).build();
			} else {
				resp = Response.status(response.getStatus()).build();
			}
		}
		return resp;
	}

	/**
	 * Processes a query of format QF2. The query is split into multiple queries (of format QF1) that are sent to the federated recommender. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param query Obfuscated query (i.e., query of format QF2). 
	 * @return Result containing a list of set of recommendations. The format is RF2. 
	 */
	private Response processObfuscatedQuery(String origin, HttpServletRequest req, JSONObject query){
		Response resp = Response.status(Response.Status.BAD_REQUEST).build();
		if (query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			JSONArray queryArray = query.getJSONArray(Cst.TAG_CONTEXT_KEYWORDS);
			Boolean oneSuccess = false; // Determines if at least one query was processed successfully
			List<JSONObject> results = new ArrayList<JSONObject>();

			// Forwarding of all the sub-queries (independently)
			for (Integer i = 0 ; i < queryArray.length() ; i++){
				JSONArray queryArrayEntry = queryArray.getJSONArray(i);
				JSONObject clonedQuery = new JSONObject(query.toString());
				if (clonedQuery.has(Cst.TAG_ID)){
					clonedQuery.put(Cst.TAG_ID, clonedQuery.get(Cst.TAG_ID) + i.toString());
				}
				clonedQuery.put(Cst.TAG_CONTEXT_KEYWORDS, queryArrayEntry);
				Response respClonedQuery = processQuery(origin, req, clonedQuery, QueryFormats.QF1);
				Boolean success = (respClonedQuery.getStatus() == Response.Status.OK.getStatusCode());
				oneSuccess = oneSuccess || success;
				JSONObject result = new JSONObject();
				if (success){
					result = new JSONObject(respClonedQuery.getEntity().toString());
				}
				results.add(result);
			}
			// Returns the results
			if (oneSuccess){
				// Merges the results corresponding to the queries
				JSONObject mergedResults = mergeResults(results);
				resp = Response.ok().entity(mergedResults.toString()).build();
			} else {
				resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			resp = Response.status(Response.Status.BAD_REQUEST).build();
		}
		return resp;
	}
	
	/**
<<<<<<< HEAD
	 * TODO
	 * This method ensures that the "detail" attribute is well-formed. 
	 * Ideally it should not be here. 
	 * @param jsonString
	 * @return
=======
	 * This method ensures that the "detail" attribute is well-formed. 
	 * Ideally it should not be here (it should be done on the federated recommender). 
	 * @param jsonString The JSON string to be corrected. 
	 * @return A JSON string. 
>>>>>>> dev
	 */
	protected String correctDetailField(String jsonString){ 
		JSONObject tempResponse = new JSONObject(jsonString);
		if (tempResponse.has(Cst.TAG_DOCUMENT_BADGE)){
			JSONArray tempBadges = tempResponse.getJSONArray(Cst.TAG_DOCUMENT_BADGE);
			for (int i = 0 ; i < tempBadges.length() ; i++){
				
				JSONObject tempBadge = tempBadges.getJSONObject(i);	
				if (tempBadge.has(Cst.TAG_DETAIL)){ 
					String tempDetail = tempBadge.getString(Cst.TAG_DETAIL); 
					JSONObject newDetail = new JSONObject(tempDetail); 
					tempBadge.put(Cst.TAG_DETAIL, newDetail); 
				}
			}
		} 
		jsonString = tempResponse.toString();
		return jsonString;
	}

	/**
	 * Merges results into a single result: {@code mergeResults([r1, r2, r3])} returns {@code r4} where r1, r2 and r3 are of format RF1 and r4 is of format RF2.  
	 * @param results A list of results. Each element is in format RF1. 
	 * @return A result of format RF2. The ordering of sub-results is kept. 
	 */
	private JSONObject mergeResults(List<JSONObject> results){
		JSONObject mergedResults = new JSONObject();
		mergedResults.put(Cst.TAG_PROVIDER, Cst.RECOMMENDER_LABEL); 
		JSONArray resultsArray = new JSONArray();
		Integer totalNbResults = 0;
		for (Integer i = 0 ; i < results.size() ; i++){
			JSONObject result = results.get(i);
			JSONArray resultValue = new JSONArray();
			if (result.has(Cst.TAG_RESULT)){
				resultValue = result.getJSONArray(Cst.TAG_RESULT);
				totalNbResults += resultValue.length();
			}
			resultsArray.put(i, resultValue);
		}
		mergedResults.put(Cst.TAG_NB_RESULTS, totalNbResults);
		mergedResults.put(Cst.TAG_RESULT, resultsArray);
		return mergedResults;
	}

}