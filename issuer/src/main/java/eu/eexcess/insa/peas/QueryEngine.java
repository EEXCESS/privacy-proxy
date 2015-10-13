package eu.eexcess.insa.peas;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.eexcess.Cst;
import eu.eexcess.insa.QueryFormats;
import eu.eexcess.insa.RequestForwarder;

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
	public JSONObject alterQuery(JSONObject query){
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
	public Response processQuery(JSONObject query, QueryFormats type, UriInfo uriInfo){
		Response resp = Response.status(Status.BAD_REQUEST).build();
		if (type.equals(QueryFormats.QF1)) {
			QueryLogThread thread = new QueryLogThread();
			thread.log(query);
		} 
		if (type.equals(QueryFormats.QF2)){
			resp = processObfuscatedQuery(query, uriInfo);
		} else if (type.equals(QueryFormats.QF1) || type.equals(QueryFormats.QF3)){
			resp = processRegularQuery(query, type, uriInfo);
		}
		return resp;
	}

	protected Response processRegularQuery(JSONObject query, QueryFormats type, UriInfo uriInfo){
		Response resp = Response.status(Response.Status.BAD_REQUEST).build();
		String serviceUrl = Cst.SERVICE_RECOMMEND;
		if (type.equals(QueryFormats.QF3)){
			serviceUrl = Cst.SERVICE_GET_DETAILS;
		} 
		resp = RequestForwarder.forwardPostRequest(serviceUrl, MediaType.APPLICATION_JSON, String.class, uriInfo.getQueryParameters(), query.toString());
		if (resp.getStatus() == Response.Status.OK.getStatusCode()){
			if (type.equals(QueryFormats.QF3)){
				String output = resp.getEntity().toString();
				output = correctDetailField(output);
				resp = Response.ok().entity(output).build();
			}
		} else {
			resp = Response.status(resp.getStatus()).build();
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
	protected Response processObfuscatedQuery(JSONObject query, UriInfo uriInfo){
		Response resp = Response.status(Response.Status.BAD_REQUEST).build();
		if (query.has(Cst.TAG_CONTEXT_KEYWORDS)){
			JSONArray queryArray = query.getJSONArray(Cst.TAG_CONTEXT_KEYWORDS);
			Boolean oneSuccess = false; // Determines if at least one query was processed successfully
			List<JSONObject> results = new ArrayList<JSONObject>();
			Integer nbResults = 0;
			
			List<QueryEngineThread> threads = new ArrayList<QueryEngineThread>();
			
			// Forwarding of all the sub-queries (independently)
			for (Integer i = 0 ; i < queryArray.length() ; i++){
				JSONArray queryArrayEntry = queryArray.getJSONArray(i);
				JSONObject clonedQuery = new JSONObject(query.toString());
				if (clonedQuery.has(Cst.TAG_QUERY_ID)){
					clonedQuery.put(Cst.TAG_QUERY_ID, clonedQuery.get(Cst.TAG_QUERY_ID) + i.toString());
				}
				clonedQuery.put(Cst.TAG_CONTEXT_KEYWORDS, queryArrayEntry);
				
				QueryEngineThread thread = new QueryEngineThread(this, clonedQuery, QueryFormats.QF1, uriInfo);
				threads.add(thread);
				thread.start();

			}
			for (Integer i = 0 ; i < threads.size() ; i++){
				QueryEngineThread thread = threads.get(i);
				try {
					thread.join();
					Response respClonedQuery = thread.getReponse();
					Boolean success = (respClonedQuery.getStatus() == Response.Status.OK.getStatusCode());
					oneSuccess = oneSuccess || success;
					if (success){
						JSONObject result = new JSONObject(respClonedQuery.getEntity().toString());
						result.remove(Cst.TAG_QUERY_ID);
						nbResults += result.getJSONArray(Cst.TAG_RESULT).length();
						results.add(result);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Returns the results
			if (oneSuccess){
				JSONObject globalResult = new JSONObject();
				globalResult.put(Cst.TAG_RESULTS, results);
				if (query.has(Cst.TAG_QUERY_ID)){
					globalResult.put(Cst.TAG_QUERY_ID, query.getString(Cst.TAG_QUERY_ID));
				}
				resp = Response.ok().entity(globalResult.toString()).build();
			} else {
				resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			resp = Response.status(Response.Status.BAD_REQUEST).build();
		}
		return resp;
	}
	
	/**
	 * This method ensures that the "detail" attribute is well-formed. 
	 * Ideally it should not be here (it should be done on the federated recommender). 
	 * @param jsonString The JSON string to be corrected. 
	 * @return A JSON string. 
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

}