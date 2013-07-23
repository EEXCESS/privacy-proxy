package eu.eexcess.insa.proxy.connectors;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class MendeleyDocumentQueryMapper implements Processor { 

	final private String consummerKey = "e13d6dfff14e3174b84d9bca29cd6082051d51fd6";

/*
 * 
 * doc :  http://apidocs.mendeley.com/home/public-resources/search-details
 * 
 * (non-Javadoc)
 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
 */
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);
		
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	    StringBuffer query = new StringBuffer();
	    JsonNode nodeBuffer;
	    String queryEndpoints ="";
	    String queryEndpointsDelimiter =",";
	    Iterator<JsonNode> itNodes = rootNode.path("documents").getElements();
	    int numberOfQueries = 0;
	    while ( itNodes.hasNext()){
	    	nodeBuffer = itNodes.next();
	    	if(!nodeBuffer.path("uuid").isMissingNode()){
	    		queryEndpoints+= "http4://api.mendeley.com/oapi/documents/details/"+nodeBuffer.path("uuid").asText()+"?consumer_key="+consummerKey+queryEndpointsDelimiter;
	    		numberOfQueries++;
	    	}
	    }
	   in.setBody(null);
	    in.setHeader(Exchange.HTTP_METHOD, "GET");
	    in.setHeader("QueryEndpoint",queryEndpoints);
	}

}
