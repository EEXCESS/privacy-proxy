package eu.eexcess.insa;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;

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
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

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
	private static final String PATH_RECOMMEND_EU = "/recommendEU";
	private static final String PATH_LOG = "/log/";
	private static final String PATH_DISAMBIGUATE = "/disambiguate";
	private static final String PATH_INTERACTION_TYPE = "{InteractionType}";

	private static final String RATING = "rating";
	private static final String RESULT_CLOSE = "rclose";
	private static final String RESULT_VIEW = "rview";
	private static final String SHOW_HIDE = "show_hide";
	private static final String FACET_SCAPE = "facetScape";
	private static final String QUERY_ACTIVATED = "query_activated";
	
	private static final String FACET_SCAPE_LOGGER = "facetScapeLogger";
	
	private static final String federatedRecommenderAPI = "http://eexcess.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	private static final String europeanaAPIprefix = "http://europeana.eu/api//v2/search.json?wskey=HT6JwVWha&query=";
	private static final String europeanaAPIsuffix = "&start=1&rows=48&profile=standard";
	private static final String disambiguationAPI = "http://zaire.dimis.fim.uni-passau.de:9393/code-server/disambiguation/categorysuggestion";
	private static final Logger logger = Logger.getLogger(PrivacyProxyService.class.getName());
	private static final Logger facetScapeLogger = Logger.getLogger(FACET_SCAPE_LOGGER);
	private static final ProxyLogProcessor plp = new ProxyLogProcessor();
	private static final JsonFactory factory = new JsonFactory();

	@POST
	@Path(PATH_RECOMMEND)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSON(@HeaderParam(Cst.TAG_ORIGIN) String origin,
			String input, 
			@Context HttpServletRequest req) {
		Response resp = null;
		try {
			JsonParser jp = factory.createJsonParser(input);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode query = mapper.readValue(jp, ObjectNode.class);
			// check for / add queryID
			JsonNode queryID = query.path(Cst.TAG_QUERY_ID);
			if(queryID.isMissingNode()) {
				String queryHash = "" + query.path(Cst.TAG_CONTEXT_KEYWORDS).toString().hashCode();
				queryHash += System.currentTimeMillis();
				queryID = new TextNode(queryHash);
				query.put(Cst.TAG_QUERY_ID, queryID);
			}
			if(origin == null) {
				origin = "empty";
			}
			JsonNode originNode = query.path(Cst.TAG_ORIGIN);
			if(!originNode.isMissingNode()) {
				origin = originNode.toString();
				query.remove(Cst.TAG_ORIGIN);
			}
			// Log the query
			plp.process(InteractionType.QUERY, origin, req.getRemoteAddr(), query.toString());
			// remove UUID
			input = query.toString();
			query.remove(Cst.TAG_UUID);
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
	@Path(PATH_RECOMMEND_EU)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response responseJSONeu(@HeaderParam(Cst.TAG_ORIGIN) String origin,
			String input, 
			@Context HttpServletRequest req) {
		Response resp = null;
		try {
			JsonParser jp = factory.createJsonParser(input);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode query = mapper.readValue(jp, ObjectNode.class);
			// check for / add queryID
			JsonNode queryID = query.path(Cst.TAG_QUERY_ID);
			if(queryID.isMissingNode()) {
				String queryHash = "" + query.path(Cst.TAG_CONTEXT_KEYWORDS).toString().hashCode();
				queryHash += System.currentTimeMillis();
				queryID = new TextNode(queryHash);
				query.put(Cst.TAG_QUERY_ID, queryID);
			}
			if(origin == null) {
				origin = "empty";
			}
			JsonNode originNode = query.path(Cst.TAG_ORIGIN);
			if(!originNode.isMissingNode()) {
				origin = originNode.toString();
				query.remove(Cst.TAG_ORIGIN);
			}
			// Log the query
			plp.process(InteractionType.QUERY, origin, req.getRemoteAddr(), query.toString());
			
			
			// build query for europeana
			String euQuery = "";
			for(Iterator<JsonNode> i = query.path(Cst.TAG_CONTEXT_KEYWORDS).getElements(); i.hasNext();) {
				JsonNode keyword = i.next();
				euQuery += keyword.path("text").asText() + " ";
			}
			euQuery = URLEncoder.encode(euQuery, "UTF-8").replaceAll("\\+", "%20");
			String europeanaAPI = europeanaAPIprefix + euQuery + europeanaAPIsuffix;
			
			
			// Forward the query
			Client client = Client.create();
			WebResource webResource = client.resource(europeanaAPI);
			ClientResponse response = webResource
					.accept(MediaType.APPLICATION_JSON)
					.type(Cst.TYPE_APPLICATION)
					.get(ClientResponse.class);
			
			
			String output = response.getEntity(String.class);
			
			// create federated recommender response format from europeana result
			JsonParser op = factory.createJsonParser(output);
			JsonNode res = mapper.readValue(op, JsonNode.class);
			ObjectNode root = mapper.createObjectNode();
			root.put("provider", new TextNode("europeana"));
			ArrayNode results = mapper.createArrayNode();

			root.put("result", results);
			for(Iterator<JsonNode> i = res.path("items").getElements(); i.hasNext();) {
				JsonNode result = i.next();
				ObjectNode r = mapper.createObjectNode();
				r.put("id", result.path("id"));
				JsonNode title = result.path("title");
				if(title.isMissingNode()) {
					r.put("title", "no title");
				} else {
					if(title.isArray()) {
						r.put("title", title.get(0));
					} else {
						r.put("title", title);
					}
				}
				r.put("uri", result.path("guid"));
				r.put("eexcessURI", result.path("guid"));
				JsonNode preview = result.path("edmPreview");
				if(preview.isArray()) {
					r.put("previewImage", preview.get(0));
				} else if(!preview.isMissingNode()) {
					r.put("previewImage", preview);
				}
				
				// facets
				ObjectNode facets = mapper.createObjectNode();
				facets.put("provider", new TextNode("Europeana"));
				String[]f = {"type","subject","year","language","contributor","dataProvider","rights","ugc","usertags"};
				for(int k = 0; k< f.length; k++) {
					JsonNode facet = result.path(f[k]);
					if(!facet.isMissingNode()) {
						if(facet.isArray()) {
							facets.put(f[k], facet.get(0));
						} else {
							facets.put(f[k], facet);
						}
					}
				}
				r.put("facets", facets);
				results.add(r);
			}
			root.put("totalResults", results.size());

			switch (response.getStatus()) {
			case 200:
			case 201:
				plp.process(InteractionType.RESULT, origin, req.getRemoteAddr(), input, root.toString());
				resp = Response.status(Cst.WS_200).entity(root.toString()).build();
				break;
			default:
				String msg = Util.sBrackets(PATH_RECOMMEND_EU) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_HTTP_ERR_CODE, response.getStatus()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ output;
				logger.error(msg);
				resp = Response.status(Cst.WS_500).build();
				break;
			}
		} catch (JsonParseException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND_EU) + Cst.SPACE
					+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE 
					+ e;
			logger.error(msg);
			resp = Response.status(Cst.WS_500).entity(Cst.ERR_MSG_PARSE_JSON).build();
		} catch (IOException e) {
			String msg = Util.sBrackets(PATH_RECOMMEND_EU) + Cst.SPACE
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
		if (response.getStatus() == Cst.WS_201 || response.getStatus() == Cst.WS_200) {
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
