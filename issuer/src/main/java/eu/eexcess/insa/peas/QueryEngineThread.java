package eu.eexcess.insa.peas;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import eu.eexcess.insa.QueryFormats;

/**
 * This class is used to parallelize the processing of the sub-queries constituting an obfuscated query. 
 * @author Thomas Cerqueus
 * @version 1.0
 */
public class QueryEngineThread extends Thread {

	// Query engine
	private QueryEngine queryEngine;
	
	// Inputs
	private JSONObject query; 
	private QueryFormats type; 
	private UriInfo uriInfo; 
	
	// Output
	private Response response;
	
	/**
	 * Constructor. 
	 * @param queryEngine The engine in charge of processing the query (the caller). 
	 * @param query The query to be processed. 
	 * @param type Format of the query (QF1, QF2 or QF3). 
	 * @param uriInfo URI information. 
	 */
	public QueryEngineThread(QueryEngine queryEngine, JSONObject query, QueryFormats type, UriInfo uriInfo){
		this.queryEngine = queryEngine;
		this.query = query;
		this.type = type;
		this.uriInfo = uriInfo;
	}
	
	/**
	 * @return The response corresponding to the query. 
	 */
	public Response getReponse(){
		return response;
	}
	
	@Override
	public void run(){
		response = queryEngine.processRegularQuery(query, type, uriInfo);
	}

}
