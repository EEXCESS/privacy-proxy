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

/**
 * Two formats of queries are handled in this component: 
 * <ul>
 * 	<li>QF1: allows to represent queries of the form q = (t1 t2). This format is the only one handled by the federated recommender.</li>
 * 	<li>QF2: allows to represent composed queries of the form q = [(t1 t2), (t3 t4), (t5 t6)]. </li> 
 * </ul>
 * Two formats of results are handled in this component: 
 * <ul>
 * 	<li>RF1: represents a result of the form r = [r1, r2, r3]. This format is the one returned by the federated recommender.</li>
 *  <li>RF2: represents a result of the form r = [[r1, r2, r3], [r4, r5, r6], [r7, r8, r9]]. </li>
 * </ul>
 * Queries of format QF1 executed with {@code processQuery} (respectively QF2 executed with {@code processObfuscatedQuery}) will return a result of format RF1 (respectively RF2). 
 * @author Thomas Cerqueus
 * @version 2.0
 */
public class QueryEngine {

	/** Default constructor. */
	public QueryEngine(){}

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
	 * Process a query of format QF1. The query is sent to the federated recommender. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param query Query of format QF1. 
	 * @return Result containing a set of recommendations. The format is RF1. 
	 */
	public Response processQuery(String origin, HttpServletRequest req, JSONObject query){ 
		Response resp;
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_RECOMMEND);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, query.toString());
		String output = response.getEntity(String.class);
		Integer status = response.getStatus();

//		String msg = Util.sBrackets(Cst.PATH_RECOMMEND) + Cst.SPACE
//				+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
//				+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE + output;
		if (status.equals(Response.Status.OK.getStatusCode())){
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.ok().entity(output).build();
		} else if (status.equals(Response.Status.CREATED.getStatusCode())){
//			Cst.LOG_PROCESSOR.process(Cst.RESULT, origin, req.getRemoteAddr(), query.toString(), output);
			resp = Response.status(response.getStatus()).entity(output).build();
		} else {
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(response.getStatus()).build();
		}
		return resp;
	}

	/**
	 * Process a query of format QF2. The query is split into multiple queries (of format QF1) that are sent to the federated recommender. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param query Obfuscated query (i.e., query of format QF2). 
	 * @return Result containing an list of set of recommendations. The format is RF2. 
	 */
	public Response processObfuscatedQuery(String origin, HttpServletRequest req, JSONObject query){
		Response resp = null;
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
				Response respClonedQuery = processQuery(origin, req, clonedQuery);
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
	 * TODO
	 * @param origin
	 * @param req
	 * @param detailsQuery
	 * @return
	 */
	// XXX This method is very similar to processQuery
	public Response processDetailsQuery(String origin, HttpServletRequest req, JSONObject detailsQuery){ 
		Response resp = null;
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_GET_DETAILS);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, detailsQuery.toString());
		String output = response.getEntity(String.class);
		Integer status = response.getStatus();
//		String msg = Util.sBrackets(Cst.PATH_GET_DETAILS) + Cst.SPACE
//				+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
//				+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE + output;
		if (status.equals(Response.Status.OK.getStatusCode())){
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.ok().entity(output).build();
		} else if (status.equals(Response.Status.CREATED.getStatusCode())){
//			Cst.LOG_PROCESSOR.process(Cst.RESULT, origin, req.getRemoteAddr(), detailsQuery.toString(), output);
			resp = Response.status(response.getStatus()).entity(output).build();
		} else {
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(response.getStatus()).build();
		}
		return resp;
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