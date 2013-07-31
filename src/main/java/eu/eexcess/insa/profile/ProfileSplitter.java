package eu.eexcess.insa.profile;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class ProfileSplitter implements Processor {


	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);
		JsonFactory factory = new JsonFactory();
		JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode profileHits = mapper.readValue(jp, JsonNode.class);
		//System.out.println("profilehits : "+profileHits);
	    
		JsonNode hitsArray = profileHits.path("hits").path("hits");
		Iterator<JsonNode> it = hitsArray.getElements();
		while(it.hasNext()) {
			JsonNode node = it.next();
			if(node.path("_source").path("source").asText().equals("eexcess")){
				exchange.setProperty("user_id", node.path("_id").asText());
				exchange.setProperty("profile.eexcess", node.path("_source").path("profile_data"));
			}
			else if ( node.path("_source").path("source").asText().equals("mendeley") ){
				exchange.setProperty("profile.mendeley", node.path("_source").path("profile_data"));
			}
		}
		
	}

	
}
