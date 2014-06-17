package eu.eexcess.up;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class ProxyLogProcessor implements Processor {
	private static final Logger logger = Logger.getLogger(ProxyLogProcessor.class.getName());
	private InteractionType type;
	
	public ProxyLogProcessor(InteractionType type) {
		this.type = type;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		
		
		String req = exchange.getProperty("req").toString();
		String origin = exchange.getProperty("origin").toString();
		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createJsonParser(req);
	    ObjectMapper mapper = new ObjectMapper();
		JsonNode rawReq = mapper.readValue(jp, JsonNode.class);
		String userID = rawReq.path("uuid").asText();
		
		switch (this.type) {
		case RESULT:
			jp = factory.createJsonParser(body);
		    JsonNode rawResult = mapper.readValue(jp, JsonNode.class);
		    JsonNode results = rawResult.path("results");
		    String query = rawReq.path("eexcess-user-profile").path("context-list").path("context").toString();
		    String out = "{\"results\":[";
		    for (int i = 0; i < results.size(); i++) {
				JsonNode res = results.get(i);
				String provider = "\"" + res.path("facets").path("provider").asText()+ "\"";
				String id = "\"" +res.path("id").asText() + "\"";
				out += "{\"provider\":" + provider + ",\"id\":" + id + "}";
				if(i < results.size()-1) {
					out += ",";
				}
			}
		    out += "],\"query\":"+query+"}";
		    logger.trace("["+type+"] [userID:"+userID+"] [origin:"+origin+"] "+out);
			break;	
		default:
			logger.trace("["+type+"] [userID:"+userID+"] [origin:"+origin+"] "+body);	
			in.setBody(body);
			break;
		}
	}

}
