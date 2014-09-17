package eu.eexcess.insa;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

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

	private static final String RATING = "ration";
	private static final String RESULT_CLOSE = "rclose";
	private static final String RESULT_VIEW = "rview";
	private static final String SHOW_HIDE = "show_hide";
	private static final String FACET_SCAPE = "facetScape";
	private static final String QUERY_ACTIVATED = "query_activated";
	
	private static final String FACET_SCAPE_LOGGER = "facetScapeLogger";
	
	private static final String federatedRecommenderAPI = "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	private static final String disambiguationAPI = "http://zaire.dimis.fim.uni-passau.de:8282/code-disambiguationproxy/disambiguation/categorysuggestion";
	private static final Logger logger = Logger.getLogger(PrivacyProxyService.class.getName());
	private static final Logger facetScapeLogger = Logger.getLogger(FACET_SCAPE_LOGGER);
	private static final ProxyLogProcessor plp = new ProxyLogProcessor();

	@POST
	@Path(PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSON(@HeaderParam(Cst.TAG_ORIGIN) String origin,
			String input, 
			@Context HttpServletRequest req) {
		Response resp = null;
		try {
			// Remove the uuid
			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(input);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode query = mapper.readValue(jp, ObjectNode.class);
			query.remove(Cst.TAG_UUID);
			query.remove(Cst.TAG_CONTEXT);
			// Log the query
			plp.process(InteractionType.QUERY, origin, req.getRemoteAddr(), input);
			// Forward the query
			Client client = Client.create();
			WebResource webResource = client.resource(federatedRecommenderAPI);
			ClientResponse response = webResource
					.accept(MediaType.APPLICATION_JSON)
					.type(Cst.TYPE_APPLICATION)
					.post(ClientResponse.class, query.toString());
			String output = response.getEntity(String.class);

			switch (response.getStatus()) {
			case 200:
			case 201:
				plp.process(InteractionType.RESULT, origin, req.getRemoteAddr(), input, output);
				resp = Response.status(Cst.WS_200).entity(output).build();
				break;
			default:
				String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ output;
				logger.error(msg);
				resp = Response.status(Cst.WS_500).build();
				break;
			}
		} catch (JsonParseException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE 
					+ e;
			logger.error(msg);
			resp = Response.status(Cst.WS_500).entity(Cst.ERR_MSG_PARSE_JSON).build();
		} catch (IOException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE 
					+ e;
			logger.error(msg);
			resp = Response.status(Cst.WS_500).entity(Cst.ERR_MSG_PARSE_JSON).build();
		}
		return resp;
	}

	@POST
	@Path(PATH_LOG + PATH_INTERACTION_TYPE)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam(Cst.TAG_ORIGIN) String origin,
			@Context HttpServletRequest req,
			String input) {

		Response resp = Response.status(Cst.WS_200).build();
		String ip = req.getRemoteAddr();

		if (interactionType.equals(RATING)) {
			plp.process(InteractionType.RATING, origin, ip, input);
		} else if (interactionType.equals(RESULT_CLOSE)) {
			plp.process(InteractionType.RESULT_CLOSE, origin, ip, input);
		} else if (interactionType.equals(RESULT_VIEW)) {
			plp.process(InteractionType.RESULT_VIEW, origin, ip, input);
		} else if (interactionType.equals(SHOW_HIDE)) {
			plp.process(InteractionType.SHOW_HIDE, origin, ip, input);
		} else if (interactionType.equals(FACET_SCAPE)) {
			String msg = Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
					+ input;
			facetScapeLogger.trace(msg);
		} else if (interactionType.equals(QUERY_ACTIVATED)) {
			plp.process(InteractionType.QUERY_ACTIVATED, origin, ip, input);
		} else {
			String msg = Util.sBrackets(PATH_LOG + interactionType) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
					+ PATH_LOG + interactionType + Cst.SPACE + Cst.ERR_MSG_NOT_REST_API;
			logger.error(msg);
			resp = Response.status(Cst.WS_404).build();
		}
		return resp;
	}

	@POST
	@Path(PATH_DISAMBIGUATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logDisambiguate(String input) {
		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(disambiguationAPI);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).type(Cst.TYPE_APPLICATION)
				.post(ClientResponse.class, input);

		String output = response.getEntity(String.class);

		Response resp;
		if (response.getStatus() == Cst.WS_200){
			resp = null; // FIXME
		} else if (response.getStatus() == Cst.WS_201){
			resp = Response.status(Cst.WS_200).entity(output).build();
		} else {
			String msg = Util.sBrackets(PATH_DISAMBIGUATE) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE 
					+ output;
			logger.error(msg);
			resp = Response.status(Cst.WS_500).build();
		}
		
		return resp;
	}



}