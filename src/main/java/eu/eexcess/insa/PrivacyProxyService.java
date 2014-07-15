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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.eexcess.up.InteractionType;
import eu.eexcess.up.ProxyLogProcessor;

@Path("/v1")
public class PrivacyProxyService {

	private static final String federatedRecommenderAPI = "http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	private static final String disambiguationAPI = "http://zaire.dimis.fim.uni-passau.de:8282/code-disambiguationproxy/disambiguation/categorysuggestion";
	private static final Logger logger = Logger
			.getLogger(PrivacyProxyService.class.getName());
	private static final Logger facetScapeLogger = Logger
			.getLogger("facetScapeLogger");
	private static final ProxyLogProcessor plp = new ProxyLogProcessor();

	@POST
	@Path("/recommend")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSON(@HeaderParam("origin") String origin,
			String input, @Context HttpServletRequest req) {

		try {
			// Remove the uuid
			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(input);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode query = mapper.readValue(jp, ObjectNode.class);
			query.remove("uuid");
			// Log the query
			plp.process(InteractionType.QUERY, origin,  req.getRemoteAddr(), input);
			// Forward the query
			Client client = Client.create();
			WebResource webResource = client.resource(federatedRecommenderAPI);
			ClientResponse response = webResource
					.accept(MediaType.APPLICATION_JSON)
					.type("application/json")
					.post(ClientResponse.class, query.toString());
			String output = response.getEntity(String.class);

			switch (response.getStatus()) {
			case 200:
			case 201:
				plp.process(InteractionType.RESULT, origin, req.getRemoteAddr(), input, output);
				return Response.status(200).entity(output).build();
			default:
				logger.error("[/recommend] [HTTPErrorCode:"
						+ response.getStatus() + "] [origin:" + origin + "] "
						+ output);
				return Response.status(500).build();
			}
		} catch (JsonParseException e) {
			logger.error("[/recommend] [origin:" + origin + "] " + e);
			return Response.status(500).entity("Unable to parse the JSON")
					.build();
		} catch (IOException e) {
			logger.error("[/recommend] [origin:" + origin + "] " + e);
			return Response.status(500).entity("Unable to parse the JSON")
					.build();
		}
	}

	@POST
	@Path("/log/{InteractionType}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam("origin") String origin,
			@Context HttpServletRequest req,
			String input) {
		
		String ip = req.getRemoteAddr();

		if (interactionType.equals("rating")) {
			plp.process(InteractionType.RATING, origin, ip, input);
		} else if (interactionType.equals("rclose")) {
			plp.process(InteractionType.RESULT_CLOSE,origin, ip, input);
		} else if (interactionType.equals("rview")) {
			plp.process(InteractionType.RESULT_VIEW,origin, ip, input);
		} else if (interactionType.equals("show_hide")) {
			plp.process(InteractionType.SHOW_HIDE,origin, ip, input);
		} else if (interactionType.equals("facetScape")) {
			facetScapeLogger.trace("[origin:" + origin + "] [ip:"+ip+"] " + input);
		} else if (interactionType.equals("query_activated")) {
			plp.process(InteractionType.QUERY_ACTIVATED,origin, ip, input);
		} else {
			logger.error("[/log/" + interactionType + "] [origin:" + origin
					+ "] /log/" + interactionType + " not a valid REST API");
			return Response.status(404).build();
		}

		return Response.status(200).build();
	}

	@POST
	@Path("/disambiguate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logDisambiguate(String input) {
		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(disambiguationAPI);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).type("application/json")
				.post(ClientResponse.class, input);
		String output = response.getEntity(String.class);

		switch (response.getStatus()) {
		case 200:
		case 201:
			return Response.status(200).entity(output).build();
		default:
			logger.error("[/disambiguate] [HTTPErrorCode:"
					+ response.getStatus() + "] " + output);
			return Response.status(500).build();
		}
	}
}