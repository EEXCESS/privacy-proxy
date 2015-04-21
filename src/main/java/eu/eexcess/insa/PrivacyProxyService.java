package eu.eexcess.insa;

import java.io.IOException;

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

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.Cst;
import eu.eexcess.Util;
import eu.eexcess.up.InteractionType;
import eu.eexcess.up.ProxyLogProcessor;

@Path("/v1")
public class PrivacyProxyService {

	private static final String PATH_RECOMMEND = "/recommend";
	private static final String PATH_LOG = "/log/";
	private static final String PATH_DISAMBIGUATE = "/disambiguate";
	private static final String PATH_INTERACTION_TYPE = "{InteractionType}";
	private static final String PATH_GET_REGISTERED_PARTNERS = "/getRegisteredPartners";

	private static final String RATING = "rating";
	private static final String RESULT_CLOSE = "rclose";
	private static final String RESULT_VIEW = "rview";
	private static final String SHOW_HIDE = "show_hide";
	private static final String FACET_SCAPE = "facetScape";
	private static final String QUERY_ACTIVATED = "query_activated";
	
	private static final String ACCESS_CONTROL_KEY = "Access-Control-Allow-Origin";
	private static final String ACCESS_CONTROL_VALUE = "*";
	
	// Recommender
	private static final String RECOMMENDER_API_URL = "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender";
	private static final String SERVICE_RECOMMEND = RECOMMENDER_API_URL + "/recommend";
	private static final String SERVICE_GET_REGISTERED_PARTNERS = RECOMMENDER_API_URL + "/getRegisteredPartners";
	// Disambiguation
	private static final String DISAMBIGUATION_API_URL = "http://zaire.dimis.fim.uni-passau.de:9393/code-server/disambiguation";
	private static final String SERVICE_DISAMBIGUATION = DISAMBIGUATION_API_URL + "/categorysuggestion";
	// Loggers
	private static final Logger LOGGER = Logger.getLogger(PrivacyProxyService.class.getName());
	private static final Logger FACET_SCAPE_LOGGER = Logger.getLogger("facetScapeLogger");
	private static final ProxyLogProcessor PLP = new ProxyLogProcessor();
	// JSON factory
	private static final JsonFactory JSON_FACTORY = new JsonFactory();

	@POST
	@Path(PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSON(@HeaderParam(Cst.TAG_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp, 
			String input) {
		Response resp = null;
		try {
			JSONObject jsonObj = new JSONObject(input);
			JsonParser jp = JSON_FACTORY.createJsonParser(input);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode query = mapper.readValue(jp, ObjectNode.class);
			jsonObj.get(Cst.TAG_QUERY_ID);
			// check for / add queryID
			JsonNode queryID = query.path(Cst.TAG_QUERY_ID);
			if(queryID.isMissingNode()) {
				String queryHash = String.valueOf(query.path(Cst.TAG_CONTEXT_KEYWORDS).toString().hashCode());
				queryHash += System.currentTimeMillis();
				queryID = new TextNode(queryHash);
				query.put(Cst.TAG_QUERY_ID, queryID);
			}
			if (origin == null) {
				origin = "empty";
			}
			JsonNode originNode = query.path(Cst.TAG_ORIGIN);
			if (!originNode.isMissingNode()) {
				origin = originNode.toString();
				query.remove(Cst.TAG_ORIGIN);
			}
			// Log the query
			PLP.process(InteractionType.QUERY, origin, req.getRemoteAddr(), query.toString());
			// remove UUID
			input = query.toString();
			query.remove(Cst.TAG_UUID);
			// Forward the query
			Client client = Client.create();
			WebResource webResource = client.resource(SERVICE_RECOMMEND);
			ClientResponse response = webResource
					.accept(MediaType.APPLICATION_JSON)
					.type(Cst.TYPE_APPLICATION)
					.post(ClientResponse.class, query.toString());
			String output = response.getEntity(String.class);

			switch (response.getStatus()) {
			case 200:
			case 201:
				PLP.process(InteractionType.RESULT, origin, req.getRemoteAddr(), input, output);
				resp = Response.status(Cst.WS_200).entity(output).build();
				break;
			default:
				String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ output;
				LOGGER.error(msg);
				resp = Response.status(Cst.WS_500).build();
				break;
			}
		} catch (JsonParseException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE 
					+ e;
			LOGGER.error(msg);
			resp = Response.status(Cst.WS_500).entity(Cst.ERR_MSG_PARSE_JSON).build();
		} catch (IOException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE 
					+ e;
			LOGGER.error(msg);
			resp = Response.status(Cst.WS_500).entity(Cst.ERR_MSG_PARSE_JSON).build();
		}
		servletResp.setHeader(ACCESS_CONTROL_KEY, ACCESS_CONTROL_VALUE);
		return resp;
	}

	@POST
	@Path(PATH_LOG + PATH_INTERACTION_TYPE)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam(Cst.TAG_ORIGIN) String origin,
			@Context HttpServletRequest req,
			@Context HttpServletResponse servletResp,
			String input) {

		Response resp = Response.status(Cst.WS_200).build();
		String ip = req.getRemoteAddr();

		if (interactionType.equals(RATING)) {
			PLP.process(InteractionType.RATING, origin, ip, input);
		} else if (interactionType.equals(RESULT_CLOSE)) {
			PLP.process(InteractionType.RESULT_CLOSE, origin, ip, input);
		} else if (interactionType.equals(RESULT_VIEW)) {
			PLP.process(InteractionType.RESULT_VIEW, origin, ip, input);
		} else if (interactionType.equals(SHOW_HIDE)) {
			PLP.process(InteractionType.SHOW_HIDE, origin, ip, input);
		} else if (interactionType.equals(FACET_SCAPE)) {
			String msg = Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
					+ input;
			FACET_SCAPE_LOGGER.trace(msg);
		} else if (interactionType.equals(QUERY_ACTIVATED)) {
			PLP.process(InteractionType.QUERY_ACTIVATED, origin, ip, input);
		} else {
			String msg = Util.sBrackets(PATH_LOG + interactionType) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ PATH_LOG + interactionType + Cst.SPACE + Cst.ERR_MSG_NOT_REST_API;
			LOGGER.error(msg);
			resp = Response.status(Cst.WS_404).build();
		}
		servletResp.setHeader(ACCESS_CONTROL_KEY, ACCESS_CONTROL_VALUE);
		return resp;
	}

	@POST
	@Path(PATH_DISAMBIGUATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logDisambiguate(@Context HttpServletResponse servletResp, String input) {
		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(SERVICE_DISAMBIGUATION);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).type(Cst.TYPE_APPLICATION)
				.post(ClientResponse.class, input);

		String output = response.getEntity(String.class);

		Response resp;
		if (response.getStatus() == Cst.WS_201 || response.getStatus() == Cst.WS_200) {
			resp = Response.status(Cst.WS_200).entity(output).build();
		} else {
			String msg = Util.sBrackets(PATH_DISAMBIGUATE) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE 
					+ output;
			LOGGER.error(msg);
			resp = Response.status(Cst.WS_500).build();
		}
		servletResp.setHeader(ACCESS_CONTROL_KEY, ACCESS_CONTROL_VALUE);
		return resp;
	}
	
	@POST
	@Path(PATH_GET_REGISTERED_PARTNERS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredPartners(@Context HttpServletResponse servletResp) {
		Response response;
		Client client = Client.create();
		WebResource webResource = client.resource(SERVICE_GET_REGISTERED_PARTNERS);
		ClientResponse r = webResource.accept(MediaType.APPLICATION_JSON).type(Cst.TYPE_APPLICATION).get(ClientResponse.class);
		Integer status = r.getStatus();
		if (status.equals(Cst.WS_200)){
			String output = r.getEntity(String.class);
			response = Response.status(Cst.WS_200).entity(output).build();
			System.out.println("It worked!");
		} else {
			response = Response.status(status).build();
			System.out.println("Error " + String.valueOf(status));
		}
		
		servletResp.setHeader(ACCESS_CONTROL_KEY, ACCESS_CONTROL_VALUE);
		return response;
	}

}