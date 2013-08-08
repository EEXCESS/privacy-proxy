package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class ExtractFrontEndData implements Processor{

	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		in.setHeader(Exchange.HTTP_METHOD,"POST");
		
		InputStream is = in.getBody(InputStream.class);

		
		//coefficients are calculated for each term from the obsel's title 
		// ************************************************************
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    String user_id = "";
	    String uuid = "";
		if ( !rootNode.path("user").path("user_id").isMissingNode()){
			user_id = rootNode.path("user").path("user_id").asText();
			
		}
		if ( !rootNode.path("plugin").path("uuid").isMissingNode()){
			uuid = rootNode.path("plugin").path("uuid").asText();
			
		}
			
			
		
		exchange.setProperty("user_id", user_id);
		exchange.setProperty("plugin_uuid",uuid);
		exchange.setProperty("trace_recommendation", rootNode);
		
		
		
		
		
	}

}
