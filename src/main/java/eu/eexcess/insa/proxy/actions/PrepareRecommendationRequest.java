package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareRecommendationRequest implements Processor {
	static JsonFactory factory = new JsonFactory();
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
	    
	    InputStream is = in.getBody(InputStream.class);
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    String query = "q="+rootNode.path("document").path("title").asText();
	    in.setHeader(Exchange.HTTP_QUERY,query);
	    in.setHeader(Exchange.HTTP_METHOD,"GET");
	    
	    in.setBody(null);
	   /* JsonNode entryNode = rootNode.path("hits").path("hits").path(0);
	    
	    String loginValid = rootNode.path("hits").path("total").asText();
	    String email = entryNode.path("_source").path("email").asText();
	    String username = entryNode.path("_source").path("username").asText();
	    
	    String body = "{\"loginValid\": \""+loginValid+"\",\"email\":\""+email+"\",\"username\":\""+username+"\"}";
	    in.setBody(body);*/
	}

}
