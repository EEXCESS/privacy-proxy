package eu.eexcess.insa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

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
			String input) {
		// Log the query
		plp.process(InteractionType.QUERY, origin, input);
		// Forward the query
		Client client = Client.create();
		WebResource webResource = client.resource(federatedRecommenderAPI);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).type("application/json")
				.post(ClientResponse.class, input);
		String output = response.getEntity(String.class);

		switch (response.getStatus()) {
		case 200:
		case 201:
			plp.process(InteractionType.RESULT, origin, input, output);
			return Response.status(200).entity(output).build();
		default:
			logger.error("[/recommend] [HTTPErrorCode:" + response.getStatus()
					+ "] [origin:" + origin + "] " + output);
			return Response.status(500).build();
		}
	}

	@POST
	@Path("/log/{InteractionType}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response log(@PathParam("InteractionType") String interactionType,
			@HeaderParam("origin") String origin, String input) {

		if (interactionType.equals("rating")) {
			plp.process(InteractionType.RATING, origin, input);
		} else if (interactionType.equals("rclose")) {
			plp.process(InteractionType.RESULT_CLOSE, origin, input);
		} else if (interactionType.equals("rview")) {
			plp.process(InteractionType.RESULT_VIEW, origin, input);
		} else if (interactionType.equals("show_hide")) {
			plp.process(InteractionType.SHOW_HIDE, origin, input);
		} else if (interactionType.equals("facetScape")) {
			facetScapeLogger.trace("[origin:"+origin+"] "+input);
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
			logger.error("[/disambiguate] [HTTPErrorCode:" + response.getStatus()
					+ "] " + output);
			return Response.status(500).build();
		}
	}
}