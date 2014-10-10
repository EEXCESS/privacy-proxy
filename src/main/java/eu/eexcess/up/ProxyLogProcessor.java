package eu.eexcess.up;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import eu.eexcess.Cst;
import eu.eexcess.Util;

public class ProxyLogProcessor {

	private static final String INTERACTION_LOGGER = "interactionLogger";
	private static final String COMMA = ",";

	private static final Logger logger = Logger.getLogger(ProxyLogProcessor.class.getName());
	private static final Logger interactionLogger = Logger.getLogger(INTERACTION_LOGGER);

	public void process(InteractionType type, String origin, String ip, String request) {
		process(type, origin, ip, request, null);
	}

	public void process(InteractionType type, String origin, String ip, String request, String answer) {

		try {

			JsonFactory factory = new JsonFactory();
			JsonParser jp = factory.createJsonParser(request);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rawReq = mapper.readValue(jp, JsonNode.class);
			String userID = rawReq.path(Cst.TAG_UUID).asText();
			String msg; 

			switch (type) {
			case RESULT:
				jp = factory.createJsonParser(answer);
				JsonNode rawResult = mapper.readValue(jp, JsonNode.class);
				JsonNode results = rawResult.path(Cst.TAG_RESULT);
				
				ObjectNode out = mapper.createObjectNode(); // Will look like: {"results":<resultArr>], "query":q}
				
				ArrayNode resultArr = mapper.createArrayNode(); // Will look like: [{"p":a,"id":b}, {"p":c,"id":d}, ..., {"p":y,"id":z}]
				for(int i = 0; i < results.size(); i++) {
					ObjectNode result = mapper.createObjectNode();
					result.put(Cst.TAG_PROVIDER_SHORT, results.get(i).path(Cst.TAG_FACETS).path(Cst.TAG_PROVIDER));
					result.put(Cst.TAG_ID, results.get(i).path(Cst.TAG_ID));
					resultArr.add(result);
				}
				
				out.put(Cst.TAG_RESULTS, resultArr);
				out.put(Cst.TAG_QUERY_ID, rawReq.path(Cst.TAG_QUERY_ID)); 

				msg = Util.sBrackets(type.toString()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_USER_ID, userID) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
						+ out.toString();
				interactionLogger.trace(msg);
				break;
			default:
				msg = Util.sBrackets(type.toString()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_USER_ID, userID) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
						+ request;
				interactionLogger.trace(msg);
				break;
			}
		} catch (JsonParseException e) {
			String msg = Util.sBrackets(type.toString()) + Cst.SPACE + Cst.ERR_MSG_PARSE_JSON;
			logger.error(msg, e);
		} catch (IOException e) {
			String msg = Util.sBrackets(type.toString()) + Cst.SPACE + Cst.ERR_MSG_PARSE_JSON;
			logger.error(msg, e);
		}

	}
}