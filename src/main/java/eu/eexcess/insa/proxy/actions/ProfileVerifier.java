package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class ProfileVerifier implements Processor { 

	


	/* This processpr aims to create an elasticsearch query to check is there already 
	 * is an user with the given Mendeley account
	 * 
	 * The information retrieved from Mendeley are saved in their primary form into an
	 *  exchange property ( mendeleyProfile )
	 * 
	 * (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		
		String is = in.getBody(String.class);
		
		exchange.setProperty("mendeleyProfile",is);
		
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    String id = rootNode.path("main").path("profile_id").asText();
	    
	    String query = "{"+
			  "\"query\": {" +
			    "\"bool\": {"+
			      "\"must\": ["+
			        "{"+
			          "\"term\": {"+
			            "\"data.mendeley.main.profile_id\": \" + id + \""+
			          "}"+
			        "}"+
			      "]"+
			    "}"+
			  "}"+
			"}";
	    
	    
		in.setBody(query);
		
		
		
		
	   
	}

}
