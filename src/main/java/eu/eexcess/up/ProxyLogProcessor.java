package eu.eexcess.up;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

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
				String query = rawReq.path(Cst.TAG_CONTEXT_KEYWORDS).toString();

				String outResultsAux = ""; // Will look like: ["provider":a,"id":b], ["provider":c,"id":d], ..., ["provider":y,"id":z]
				for (int i = 0 ; i < results.size() ; i++) {
					JsonNode res = results.get(i);
					String provider = Util.quote(res.path(Cst.TAG_FACETS).path(Cst.TAG_PROVIDER).asText());
					String id = Util.quote(res.path(Cst.TAG_ID).asText());
					String content = Util.keyColonValue(Util.quote(Cst.TAG_PROVIDER), provider) + COMMA
							+ Util.keyColonValue(Util.quote(Cst.TAG_ID), id);
					outResultsAux += Util.cBrackets(content);
					if (i < results.size() - 1) {
						outResultsAux += COMMA;
					}
				}
				String outResults = Util.keyColonValue(Util.quote(Cst.TAG_RESULTS), Util.sBrackets(outResultsAux)); 
				String outQuery = Util.keyColonValue(Util.quote(Cst.TAG_QUERY), query); 
				String out = Util.cBrackets(outResults + COMMA + outQuery); // Will look like: {"results":[outResultsAux], "query":q}

				msg = Util.sBrackets(type.toString()) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_USER_ID, userID) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_ORIGIN, origin) + Cst.SPACE
						+ Util.sBracketsColon(Cst.TAG_IP, ip) + Cst.SPACE
						+ out;
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