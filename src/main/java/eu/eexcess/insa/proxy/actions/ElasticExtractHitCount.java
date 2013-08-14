package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class ElasticExtractHitCount implements Processor {

	JsonFactory jsonFactory = new JsonFactory();
	
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		
		JsonParser parser = jsonFactory.createJsonParser(in.getBody(InputStream.class));
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readValue(parser, JsonNode.class);
	
		Integer totalHits = node.path("hits").path("total").getIntValue();
		in.setBody(totalHits);
	}

}
