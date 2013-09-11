package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


/* This class is meant to get the user id from the message body.
 * 
 * The value is stored into the "user_id" exchange property
 * 
 * The message is supposed to be formed in the following way : 
 * 
 * 			{
 * 				id: theIdValue
 * 			}
 * 
 */

public class GetUserIdFromBody implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		InputStream is = in.getBody( InputStream.class);
		
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
		
		String user_id = rootNode.path("_id").asText();
		
		if ( user_id != null && !user_id.equals("")){
			exchange.setProperty("user_id", user_id);
		}
		
		

	}

	

}
