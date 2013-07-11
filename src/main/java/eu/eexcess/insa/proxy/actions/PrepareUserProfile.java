package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareUserProfile implements Processor {
	static JsonFactory factory = new JsonFactory();
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
	    InputStream is = in.getBody(InputStream.class);
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    JsonNode entryNode = rootNode.path("hits").path("hits").path(0);
	    
	    String id = entryNode.path("_id").asText();
	    String password = entryNode.path("_source").path("password").asText();
	    String email = entryNode.path("_source").path("email").asText();
	    String username = entryNode.path("_source").path("username").asText();
	    String title = entryNode.path("_source").path("title").asText();
	    String firstname = entryNode.path("_source").path("firstname").asText();
	    String lastname = entryNode.path("_source").path("lastname").asText();
	    String gender = entryNode.path("_source").path("gender").asText();
	    String birthdate = entryNode.path("_source").path("birthdate").asText();
	    String street = entryNode.path("_source").path("address").path("street").asText();
	    String postalcode = entryNode.path("_source").path("address").path("postalcode").asText();
	    String city = entryNode.path("_source").path("address").path("city").asText();
	    String country = entryNode.path("_source").path("address").path("country").asText();
	    
	    String body = "{\"id\": \""+id+"\",\"password\": \""+password+"\",\"email\":\""+email+"\",\"username\":\""+username+"\",\"title\":\""+title+"\",\"firstname\":\""+firstname+"\",\"lastname\":\""+lastname+"\",\"gender\":\""+gender+"\",\"birthdate\":\""+birthdate+"\",\"address\":{\"street\":\""+street+"\",\"postalcode\":\""+postalcode+"\",\"city\":\""+city+"\",\"country\":\""+country+"\"}}";
	    in.setBody(body);
	}

}