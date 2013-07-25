package eu.eexcess.insa.profile;

import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonNode;

public class ProfileSplitter implements Processor {


	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		JsonNode profileHits = in.getBody(JsonNode.class);
		
		JsonNode hitsArray = profileHits.path("hits").path("hits");
		Iterator<JsonNode> it = hitsArray.getElements();
		while(it.hasNext()) {
			JsonNode node = it.next();
			exchange.setProperty(key, value);
		}
		
	}

	
}
