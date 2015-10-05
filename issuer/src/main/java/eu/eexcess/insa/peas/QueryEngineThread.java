package eu.eexcess.insa.peas;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import eu.eexcess.insa.QueryFormats;

// TODO Documentation
public class QueryEngineThread extends Thread {

	// Query engine
	private QueryEngine queryEngine;
	
	// Inputs
	private JSONObject query; 
	private QueryFormats type; 
	private UriInfo uriInfo; 
	
	// Output
	private Response response;
	
	public QueryEngineThread(QueryEngine queryEngine, JSONObject query, QueryFormats type, UriInfo uriInfo){
		this.queryEngine = queryEngine;
		this.query = query;
		this.type = type;
		this.uriInfo = uriInfo;
	}
	
	public Response getReponse(){
		return response;
	}
	
	@Override
	public void run(){
		response = queryEngine.processRegularQuery(query, type, uriInfo);
	}
	

}
