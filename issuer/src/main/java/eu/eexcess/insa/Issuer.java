package eu.eexcess.insa;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.JsonUtil;
import eu.eexcess.insa.logging.Logger;
import eu.eexcess.insa.peas.CacheReader;
import eu.eexcess.insa.peas.Clique;
import eu.eexcess.insa.peas.CoOccurrenceGraph;
import eu.eexcess.insa.peas.QueryEngine;
import eu.eexcess.insa.peas.Scheduler;

/**
 * This class defines all the services offered by the Privacy Proxy. 
 * @author Thomas Cerqueus
 * @version 2.0
 */
@Path(Cst.PATH)
public class Issuer {

	protected String queryLogLocation = Config.getValue(Config.DATA_DIRECTORY) + Config.getValue(Config.QUERY_LOG);
	protected ComplianceManager complianceManager = new ComplianceManager();

	/**
	 * Initialization of the Privacy Proxy. 
	 */
	public Issuer(){
		Scheduler.addCachesTasks();
		Scheduler.flushOutQueryLogTask();
	}

	/**
	 * Service providing recommendations for a query. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param query Query of format QF1 or QF2 (both are supported). 
	 * @return A set of recommendations. The format is RF1 (respectively RF2) if the format of the query is QF1 (respectively QF2). 
	 * @see QueryEngine
	 */
	@POST
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			@Context UriInfo uriInfo,
			String query) {

		Response resp = Response.ok().build();
		JSONObject jsonQuery = new JSONObject(query);
		
		if (complianceManager.containsCompliantOrigin(jsonQuery)){
			
			Logger logger = Logger.getInstance();
			
			JSONObject jsonOrigin = jsonQuery.getJSONObject(Cst.TAG_ORIGIN);
			jsonQuery.remove(Cst.TAG_ORIGIN);
			
			jsonQuery.remove(Cst.TAG_LOGGING_LEVEL);
			String ip = req.getRemoteAddr();
			String queryId;
			if (jsonQuery.has(Cst.TAG_QUERY_ID)){
				queryId = jsonQuery.getString(Cst.TAG_QUERY_ID);
			} else {
				queryId = QueryEngine.generateQueryId(jsonQuery);
				jsonQuery.put(Cst.TAG_QUERY_ID, queryId);
			}
			logger.logQuery(jsonOrigin, ip, jsonQuery, queryId);
			jsonQuery.remove(Cst.TAG_LOGGING_LEVEL);

			QueryEngine engine = QueryEngine.getInstance();
			jsonQuery = engine.alterQuery(jsonQuery);
			if (engine.isObfuscatedQuery(jsonQuery)){
				resp = engine.processQuery(jsonQuery, QueryFormats.QF2, uriInfo);
				JSONObject results = new JSONObject(resp.getEntity().toString());
				logger.logMergedResults(jsonOrigin, ip, queryId, results);
			} else {
				resp = engine.processQuery(jsonQuery, QueryFormats.QF1, uriInfo);
				JSONObject results = new JSONObject(resp.getEntity().toString());
				logger.logRegularResults(jsonOrigin, ip, queryId, results);
			}
		} else {
			resp = complianceManager.notCompliantRequestResponse(); 
		}
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * Default service providing recommendations for a query. 
	 * It does not do anything else than returning the header. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 * @see QueryEngine
	 */
	@OPTIONS
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}

	/**
	 * Service providing detailed information of a set of resources. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param detailsQuery
	 * @return A set of detailed results (i.e., document badges). 
	 */
	@POST
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@Context HttpServletRequest req, @Context HttpServletResponse servletResp, @Context UriInfo uriInfo, String detailsQuery) {
		Response resp = Response.ok().build();
		JSONObject jsonDetailsQuery = new JSONObject(detailsQuery);
		Logger logger = Logger.getInstance();
		if (complianceManager.containsCompliantOrigin(jsonDetailsQuery) && jsonDetailsQuery.has(Cst.TAG_QUERY_ID)){
			JSONObject jsonOrigin = jsonDetailsQuery.getJSONObject(Cst.TAG_ORIGIN);
			jsonDetailsQuery.remove(Cst.TAG_ORIGIN); 
			jsonDetailsQuery.remove(Cst.TAG_LOGGING_LEVEL); 
			String queryId = jsonDetailsQuery.getString(Cst.TAG_QUERY_ID);
			jsonDetailsQuery.remove(Cst.TAG_QUERY_ID); 
			String ip = req.getRemoteAddr();
			logger.logDetailsQuery(jsonOrigin, ip, queryId, jsonDetailsQuery);

			QueryEngine engine = QueryEngine.getInstance();
			resp = engine.processQuery(jsonDetailsQuery, QueryFormats.QF3, uriInfo);
			if (resp.getStatus() == Response.Status.OK.getStatusCode()){
				JSONObject results = new JSONObject(resp.getEntity().toString());
				results.put(Cst.TAG_QUERY_ID, queryId);
				resp = Response.ok().entity(results.toString()).build();
				logger.logDetailsResults(jsonOrigin, ip, queryId, results);
			} else {
				resp = Response.status(resp.getStatus()).build();
			}
		} else {
			resp = Response.status(Status.BAD_REQUEST).build();
		}
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * Default service providing detailed information of a set of resources. 
	 * It does not do anything else than returning the header. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_GET_DETAILS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDetails(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}

	/** 
	 * Service logging interactions. 
	 * @param interactionType
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp HTTP response. 
	 * @param input Content to log. 
	 * @return An empty response with status OK. 
	 */
	@POST
	@Path(Cst.PATH_LOG + "/{" + Cst.PARAM_INTERACTION_TYPE + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam(Cst.PARAM_INTERACTION_TYPE) String interactionType,
			@Context HttpServletRequest req, @Context HttpServletResponse servletResp,
			String input) {

		Response resp = Response.ok().build();
		JSONObject jsonInput = new JSONObject(input);
		if (complianceManager.containsCompliantOrigin(jsonInput)){
			Logger logger = Logger.getInstance();
			jsonInput.put(Cst.TAG_IP, req.getRemoteAddr());
			Boolean logged = logger.log(interactionType, jsonInput); 
			if (!logged){
				resp = Response.serverError().build();
			}
		} else {
			resp = Response.status(Status.BAD_REQUEST).build();
		}
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * Default service logging interactions. 
	 * It does not do anything else than returning the header. 
	 * @param servletResp HTTP response. 
	 * @return An empty response with status OK. 
	 */
	@OPTIONS
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}

	/**
	 * Service providing access to the co-occurrence graph. 
	 * The co-occurrence graph is supposed to be up-to-date at any time (no caching).   
	 * @param servletResp HTTP response. 
	 * @return A co-occurrence graph. 
	 * @see eu.eexcess.insa.peas.CacheReader
	 */
	@GET
	@Path(Cst.PATH_GET_CO_OCCURRENCE_GRAPH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoOccurrenceGraph(@Context HttpServletResponse servletResp) {
		CacheReader cacheReader = CacheReader.getInstance();
		CoOccurrenceGraph graph = cacheReader.getCoOccurrenceGraph();
		String output = graph.toJsonString();

		Response resp = Response.ok().entity(output).build();

		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		return resp;
	}

	/**
	 * Services providing the set of cliques contained in the co-occurrence graph. 
	 * Each clique is a graph. 
	 * As the computation of cliques is very time-consuming, a cached version is returned. 
	 * The frequency at which the cache is updated is not fixed (yet): every day, every hour, etc.  
	 * @param servletResp HTTP response. 
	 * @return A set of cliques. 
	 * @see eu.eexcess.insa.peas.Clique
	 */
	@GET
	@Path(Cst.PATH_GET_CLIQUES)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaximalCliques(@Context HttpServletResponse servletResp) {
		Response resp;
		CacheReader cacheReader = CacheReader.getInstance();
		List<Clique> cliques = cacheReader.getMaximalCliques();
		String jsonCliques = "";
		for (Clique clique : cliques){
			jsonCliques += clique.toJsonString() + JsonUtil.CS;
		}
		if (jsonCliques.endsWith(JsonUtil.CS)){
			jsonCliques = jsonCliques.substring(0, jsonCliques.length() - JsonUtil.CS.length());
		}
		jsonCliques = JsonUtil.sBrackets(jsonCliques);
		resp = Response.ok().entity(jsonCliques).build();

		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
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
	public Response getRegisteredPartners(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response response = RequestForwarder.forwardGetRequest(Cst.SERVICE_GET_REGISTERED_PARTNERS, MediaType.APPLICATION_JSON, String.class, uriInfo.getQueryParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return response;
	}

	/**
	 * Service providing the favicon of a given partner. 
	 * @param servletResp HTTP response. 
	 * @param partnerId Identifier of a partner. 
	 * @return An image (image/png). 
	 */
	@GET
	@Path(Cst.PATH_GET_PARTNER_FAVICON)
	@Produces(Cst.MEDIA_TYPE_IMAGE)
	public Response getPartnerFavIcon(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response response = RequestForwarder.forwardGetRequest(Cst.SERVICE_GET_PARTNER_FAVICON, Cst.MEDIA_TYPE_IMAGE, InputStream.class, uriInfo.getQueryParameters());
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return response;
	}

	/**
	 * Service providing a default image if a media is missing. 
	 * @param servletResp HTTP response. 
	 * @param type Type of the missing media. Possible values are : text, audio, 3d, image, video, other, unknown.  
	 * @return An image (image/png). 
	 */
	@GET
	@Path(Cst.PATH_GET_PREVIEW_IMAGE)
	@Produces(Cst.MEDIA_TYPE_IMAGE)
	public Response getPreviewImage(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo) {
		Response response = RequestForwarder.forwardGetRequest(Cst.SERVICE_GET_PREVIEW_IMAGE, Cst.MEDIA_TYPE_IMAGE, InputStream.class, uriInfo.getQueryParameters());	
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return response;
	}

	/**
	 * TODO
	 * @param servletResp HTTP response.
	 * @param uriInfo
	 * @param input
	 * @return
	 */
	@POST
	@Path(Cst.PATH_SUGGEST_CATEGORIES)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response suggestCategories(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo, String input) {
		Response resp = RequestForwarder.forwardPostRequest(Cst.SERVICE_SUGGEST_CATEGORIES, MediaType.APPLICATION_JSON, InputStream.class, uriInfo.getQueryParameters(), input);	
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * TODO
	 * @param servletResp HTTP response.
	 * @return
	 */
	@OPTIONS
	@Path(Cst.PATH_SUGGEST_CATEGORIES)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response suggestCategories(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}

	/**
	 * TODO
	 * @param servletResp HTTP response.
	 * @param uriInfo
	 * @param input
	 * @return
	 */
	@POST
	@Path(Cst.PATH_RECOGNIZE_ENTITY)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recognizeEntity(@Context HttpServletResponse servletResp, @Context UriInfo uriInfo, String input) {
		Response resp = RequestForwarder.forwardPostRequest(Cst.SERVICE_RECOGIZE_ENTITY, MediaType.APPLICATION_JSON, InputStream.class, uriInfo.getQueryParameters(), input);
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		return resp;
	}

	/**
	 * TODO
	 * @param servletResp HTTP response. 
	 * @param uriInfo
	 * @return
	 */
	@OPTIONS
	@Path(Cst.PATH_RECOGNIZE_ENTITY)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recognizeEntity(@Context HttpServletResponse servletResp) {
		servletResp.setHeader(Cst.ACA_ORIGIN_KEY, Cst.ACA_ORIGIN_VALUE);
		servletResp.setHeader(Cst.ACA_HEADERS_KEY, Cst.ACA_HEADERS_VALUE);
		servletResp.setHeader(Cst.ACA_METHODS_KEY, Cst.ACA_POST);
		return Response.ok().build();
	}
	
}