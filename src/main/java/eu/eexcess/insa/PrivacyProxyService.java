package eu.eexcess.insa;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.Cst;
import eu.eexcess.JsonUtil;
import eu.eexcess.insa.peas.Clique;
//import eu.eexcess.Util;
import eu.eexcess.insa.peas.CoOccurrenceGraph;
import eu.eexcess.insa.peas.Dictionary;
import eu.eexcess.insa.peas.QueryEngine;

/**
 * This class defines all the services offered by the Privacy Proxy. 
 * @author Thomas Cerqueus
 * @version 2.0
 */
@Path(Cst.VERSION)
public class PrivacyProxyService {

	/**
	 * Service providing recommendations for a query. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param queryStr Query of format QF1 or QF2 (both are supported). 
	 * @return Result containing a set of recommendations. The format is RF1 (respectively RF2) if the format of the query is QF1 (respectively QF2). 
	 * @see QueryEngine
	 */
	@POST
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			String queryStr) {

		Response resp;
		
		JSONObject query = new JSONObject(queryStr);
		
		if (origin == null) { origin = Cst.EMPTY_ORIGIN; }
		
		QueryEngine engine = new QueryEngine();
		query = engine.alterQuery(origin, query);
		if (engine.isObfuscatedQuery(query)){
			resp = engine.processQuery(origin, req, query, QueryFormats.QF2);
		} else {
			resp = engine.processQuery(origin, req, query, QueryFormats.QF1);
		}
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}

	/**
	 * Default service providing recommendations for a query. 
	 * It does not do anything else than returning the header. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 * @see QueryEngine
	 */
	@OPTIONS
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACCESS_CONTROL_ALLOW_KEY, Cst.ACCESS_CONTROL_ALLOW_POST);
		return Response.ok().build();
	}
	
	/**
	 * Service providing detailed information of a set of resources. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param detailsStr
	 * @return TODO
	 */
	@POST
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			String detailsStr) {

		JSONObject detailsQuery = new JSONObject(detailsStr);
		
		if (origin == null) { origin = Cst.EMPTY_ORIGIN; }
		
		QueryEngine engine = new QueryEngine();
		Response resp = engine.processQuery(origin, req, detailsQuery, QueryFormats.QF3);
		
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}
	
	/**
	 * Default service providing detailed information of a set of resources. 
	 * It does not do anything else than returning the header. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		Response resp = Response.ok().build();
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACCESS_CONTROL_ALLOW_KEY, Cst.ACCESS_CONTROL_ALLOW_POST);
		return resp;
	}

	/**
	 * XXX Not sure this service is used. 
	 * Service logging interactions. 
	 * @param interactionType
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param input XXX Not sure this parameter is used. 
	 * @return TODO
	 */
	@POST
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp,
			String input) {

		Response resp = Response.ok().build();
		/*
//		String ip = req.getRemoteAddr();

		if (interactionType.equals(Cst.RATING) || 
				interactionType.equals(Cst.RESULT_CLOSE) || 
				interactionType.equals(Cst.RESULT_VIEW) ||
				interactionType.equals(Cst.SHOW_HIDE) ||
				interactionType.equals(Cst.QUERY_ACTIVATED)){
			//Cst.LOG_PROCESSOR.process(interactionType, origin, ip, input);
		} else if (interactionType.equals(Cst.FACET_SCAPE)) {
//			String msg = Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
//					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
//					+ input;
//			Cst.LOGGER_FACET_SCAPE.trace(msg);
		} else {
//			String msg = Util.sBrackets(Cst.PATH_LOG_DIRECTORY + interactionType) + Cst.SPACE
//					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
//					+ Cst.PATH_LOG_DIRECTORY + interactionType + Cst.SPACE + Cst.ERR_MSG_NOT_REST_API;
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(Response.Status.NOT_FOUND).build();
		}
		*/
		
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}
	
	/**
	 * XXX Not sure this service is used. 
	 * Default service logging interactions. 
	 * It does not do anything else than returning the header. 
	 * @param interactionType  
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACCESS_CONTROL_ALLOW_KEY, Cst.ACCESS_CONTROL_ALLOW_POST);
		return Response.ok().build();
	}

	/**
	 * XXX Not sure this service is used. 
	 * Service logging disambiguation calls. 
	 * @param servletResp HTTP response. 
	 * @param input XXX Don't know what this parameter is supposed to contain. 
	 * @return TODO
	 */
	@POST
	@Path(Cst.PATH_DISAMBIGUATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logDisambiguate(@Context HttpServletResponse servletResp, String input) {
		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_DISAMBIGUATION);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, input);

		String output = response.getEntity(String.class);

		Response resp;
		if (response.getStatus() == Response.Status.CREATED.getStatusCode() || response.getStatus() == Response.Status.OK.getStatusCode()) {
			resp = Response.ok().entity(output).build();
		} else {
//			String msg = Util.sBrackets(Cst.PATH_DISAMBIGUATE) + Cst.SPACE
//					+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE 
//					+ output;
//			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}

	/**
	 * XXX Not sure this service is used. 
	 * Default service logging disambiguation calls. 
	 * It does not do anything else than returning the header. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_DISAMBIGUATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logDisambiguate(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACCESS_CONTROL_ALLOW_KEY, Cst.ACCESS_CONTROL_ALLOW_POST);
		return Response.ok().build();
	}
	
	/**
	 * Service returning the list of registered partners. 
	 * This service only forwards the query to the federated recommender, 
	 * and returns the result. 
	 * @param servletResp HTTP response. 
	 * @return The list of partners registered on the federated recommender. 
	 */
	@GET
	@Path(Cst.PATH_GET_REGISTERED_PARTNERS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredPartners(@Context HttpServletResponse servletResp) {
		Response response;
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_GET_REGISTERED_PARTNERS);
		ClientResponse r = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		int status = r.getStatus();
		if (status == Response.Status.OK.getStatusCode()){
			String output = r.getEntity(String.class);
			response = Response.ok().entity(output).build();
		} else {
			response = Response.status(status).build();
		}

		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return response;
	}
	
	/**
	 * Service providing access to the co-occurrence graph. 
	 * The co-occurence graph is supposed to be up-to-date at any time (no caching).   
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return A co-occurrence graph. 
	 * @see eu.eexcess.insa.peas.CoOccurrenceGraph
	 */
	@GET
	@Path(Cst.PATH_GET_CO_OCCURRENCE_GRAPH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoOccurrenceGraph(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		
		CoOccurrenceGraph graph = new CoOccurrenceGraph();
		String output = graph.toJsonString();
		
		Response resp = Response.ok().entity(output).build();
		
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}
	
	/**
	 * Services providing the set of cliques contained in the co-occurrence graph. 
	 * Each clique is a graph. 
	 * As the computation of cliques is very time-consuming, a cached version is returned. 
	 * The frequency at which the cache is updated is not fixed (yet): every day, every hour, etc.  
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return A set of cliques. 
	 * @see eu.eexcess.insa.peas.Clique
	 */
	@GET
	@Path(Cst.PATH_GET_CLIQUES)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCliques(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		
		Response resp = null;
		
		/*String clique1 = "["
				+ "{"
					+ "\"term\":\"a\","
					+ "\"frequencies\":["
						+ "{"
							+ "\"term\":\"b\", "
							+ "\"frequency\":1"
						+ "}"
					+ "]"
				+ "}"
			+ "]";
		String clique2 = "["
				+ "{"
					+ "\"term\":\"c\","
					+ "\"frequencies\":["
						+ "{"
							+ "\"term\":\"d\", "
							+ "\"frequency\":2"
						+ "},"
						+ "{"
							+ "\"term\":\"e\", "
							+ "\"frequency\":3"
						+ "}"
					+ "]"
				+ "},"
				+ "{"
					+ "\"term\":\"d\","
					+ "\"frequencies\":["
						+ "{"
							+ "\"term\":\"e\", "
							+ "\"frequency\":4"
						+ "}"
					+ "]"
				+ "}"
			+ "]";
		String cliques = "[" + clique1 + ", " + clique2 + "]";*/
		CoOccurrenceGraph graph = new CoOccurrenceGraph();
		List<Clique> cliques = graph.getMaximalCliques();
		String jsonCliques = "";
		for (Clique clique : cliques){
			jsonCliques += clique.toJsonString() + JsonUtil.CS;
		}
		if (jsonCliques.endsWith(JsonUtil.CS)){
			jsonCliques = jsonCliques.substring(0, jsonCliques.length() - JsonUtil.CS.length());
		}
		jsonCliques = JsonUtil.sBrackets(jsonCliques);
		resp = Response.ok().entity(jsonCliques).build();
		
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}
	
	/**
	 * Service providing a dictionary. 
	 * The dictionary is expected to be static so every call will return the exact same result. 
	 * The number of entries is not very large (~4000 entries).  
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @return A set of words. 
	 */
	@GET
	@Path(Cst.PATH_GET_DICTIONARY)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDictionary(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {
		
		Dictionary dictionary = new Dictionary();
		String output = dictionary.toJsonString();
		
		Response resp = Response.ok().entity(output).build();
		
		servletResp.setHeader(Cst.ACCESS_CONTROL_ORIGIN_KEY, Cst.ACCESS_CONTROL_ORIGIN_VALUE);
		return resp;
	}
	
}