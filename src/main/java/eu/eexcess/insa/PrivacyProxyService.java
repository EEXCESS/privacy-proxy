package eu.eexcess.insa;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.Config;
import eu.eexcess.Cst;
import eu.eexcess.Util;

@Path(Cst.VERSION)
public class PrivacyProxyService {

	@POST
	@Path(Cst.PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSON(@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			String input) {
		
		Response resp = null;
		JSONObject jsonObj = new JSONObject(input);
		
		// Generate a query ID if it does not exist yet
		if (!jsonObj.has(Cst.TAG_QUERY_ID)){
			String queryHash = String.valueOf(jsonObj.get(Cst.TAG_CONTEXT_KEYWORDS).toString().hashCode());
			queryHash += System.currentTimeMillis();
			jsonObj.put(Cst.TAG_QUERY_ID, queryHash);
		}
		if (jsonObj.has(Cst.TAG_ORIGIN)){
			jsonObj.remove(Cst.TAG_ORIGIN);
		}
		input = jsonObj.toString();
		jsonObj.remove(Cst.TAG_UUID);
		String queryStr = jsonObj.toString();

		if (origin == null) {
			origin = Cst.EMPTY_ORIGIN;
		}

		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_RECOMMEND);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, queryStr);
		String output = response.getEntity(String.class);

		switch (response.getStatus()) {
		case 200:
		case 201:
			Cst.LOG_PROCESSOR.process(Cst.RESULT, origin, req.getRemoteAddr(), input, output);
			resp = Response.status(Cst.WS_200).entity(output).build();
			break;
		default:
			String msg = Util.sBrackets(Cst.PATH_RECOMMEND) + Cst.SPACE
			+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
			+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
			+ output;
			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(response.getStatus()).build();
			break;
		}

		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	@POST
	@Path(Cst.PATH_LOG)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam(Cst.PARAM_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp,
			String input) {

		Response resp = Response.status(Cst.WS_200).build();
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
			resp = Response.status(Cst.WS_404).build();
		}
		
		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

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
		if (response.getStatus() == Cst.WS_201 || response.getStatus() == Cst.WS_200) {
			resp = Response.status(Cst.WS_200).entity(output).build();
		} else {
			String msg = Util.sBrackets(Cst.PATH_DISAMBIGUATE) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE 
					+ output;
			Cst.LOGGER_PRIVACY_PROXY.error(msg);
			resp = Response.status(Cst.WS_500).build();
		}
		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return resp;
	}

	@POST
	@Path(Cst.PATH_GET_REGISTERED_PARTNERS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredPartners(@Context HttpServletResponse servletResp) {
		Response response;
		Client client = Client.create();
		WebResource webResource = client.resource(Cst.SERVICE_GET_REGISTERED_PARTNERS);
		ClientResponse r = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		Integer status = r.getStatus();
		if (status.equals(Cst.WS_200)){
			String output = r.getEntity(String.class);
			response = Response.status(Cst.WS_200).entity(output).build();
		} else {
			response = Response.status(status).build();
		}

		servletResp.setHeader(Config.getValue(Cst.ACCESS_CONTROL_KEY), Config.getValue(Cst.ACCESS_CONTROL_VALUE));
		return response;
	}

}