package eu.eexcess.up;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class ProxyLogProcessor {
	private static final Logger logger = Logger.getLogger(ProxyLogProcessor.class.getName());
	private static final Logger interactionLogger = Logger.getLogger("interactionLogger");

	public void process(InteractionType type, String origin, String request) {
		process(type, origin, request, null);
	}
	
	public void process(InteractionType type, String origin, String request, String answer) {

		try {

			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(request);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rawReq = mapper.readValue(jp, JsonNode.class);
			String userID = rawReq.path("uuid").asText();

			switch (type) {
			case RESULT:
				jp = factory.createJsonParser(answer);
				JsonNode rawResult = mapper.readValue(jp, JsonNode.class);
				JsonNode results = rawResult.path("result");
				String query = rawReq.path("contextKeywords").toString();
				String out = "{\"results\":[";
				for (int i = 0; i < results.size(); i++) {
					JsonNode res = results.get(i);
					String provider = "\""
							+ res.path("facets").path("provider").asText()
							+ "\"";
					String id = "\"" + res.path("id").asText() + "\"";
					out += "{\"provider\":" + provider + ",\"id\":" + id + "}";
					if (i < results.size() - 1) {
						out += ",";
					}
				}
				out += "],\"query\":" + query + "}";
				interactionLogger.trace("["+type+"] [userID:"+userID+"] [origin:"+origin+"] "+out);
				break;
			default:
				interactionLogger.trace("["+type+"] [userID:"+userID+"] [origin:"+origin+"] "+request);	
				break;
			}

		} catch (JsonParseException e) {
			logger.error("["+type+"] Error while parsing JSON",e);
		} catch (IOException e) {
			logger.error("["+type+"] Error while parsing JSON",e);
		}

	}
}