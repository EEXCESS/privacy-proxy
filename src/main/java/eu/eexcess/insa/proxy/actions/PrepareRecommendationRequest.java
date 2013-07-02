package eu.eexcess.insa.proxy.actions;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareRecommendationRequest implements Processor {
	static JsonFactory factory = new JsonFactory();
	public void process(Exchange exchange) throws Exception {
		
		
	   /* JsonNode entryNode = rootNode.path("hits").path("hits").path(0);
	    
	    String loginValid = rootNode.path("hits").path("total").asText();
	    String email = entryNode.path("_source").path("email").asText();
	    String username = entryNode.path("_source").path("username").asText();
	    
	    String body = "{\"loginValid\": \""+loginValid+"\",\"email\":\""+email+"\",\"username\":\""+username+"\"}";
	    in.setBody(body);*/
	}

}
