package eu.eexcess.insa;

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

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.Util;

/**
 * 
 * @author Thomas Cerqueus
 * @version 2.0
 */
@Path(Cst.VERSION)
public class PrivacyProxyService {

	/**
	 * Service providing recommendations for a query. 
	 * @param origin Origin of the query. 
	 * @param req HTTP request. 
	 * @param servletResp TODO
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
		if (engine.isDisjunctiveQuery(query)){
			resp = engine.processDisjunctiveQuery(origin, req, query);
		} else {
			resp = engine.processQuery(origin, req, query);
		}
		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	/**
	 * TODO
	 * @param origin
	 * @param req
	 * @param servletResp
	 * @return
	 */
	@OPTIONS
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp) {

		Response resp = Response.ok().build();
		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	/**
	 * TODO
	 * @param interactionType
	 * @param origin
	 * @param req
	 * @param servletResp
	 * @param input
	 * @return
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
		String ip = req.getRemoteAddr();

		if (interactionType.equals(Cst.RATING) || 
				interactionType.equals(Cst.RESULT_CLOSE) || 
				interactionType.equals(Cst.RESULT_VIEW) ||
				interactionType.equals(Cst.SHOW_HIDE) ||
				interactionType.equals(Cst.QUERY_ACTIVATED)){
			Cst.LOG_PROCESSOR.process(interactionType, origin, ip, input);
		} else if (interactionType.equals(Cst.FACET_SCAPE)) {
			String msg = Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
					+ input;
			Cst.LOGGER_FACET_SCAPE.trace(msg);
		} else {
			String msg = Util.sBrackets(Cst.PATH_LOG_DIRECTORY + interactionType) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Cst.PATH_LOG_DIRECTORY + interactionType + Cst.SPACE + Cst.ERR_MSG_NOT_REST_API;
			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(Response.Status.NOT_FOUND).build();
		}

		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	/**
	 * TODO
	 * @param servletResp
	 * @param input
	 * @return
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
			String msg = Util.sBrackets(Cst.PATH_DISAMBIGUATE) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE 
					+ output;
			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	
	/**
	 * TODO
	 * @param servletResp
	 * @return
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

		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return response;
	}

}