package eu.eexcess.insa.proxy.connectors;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class EconBizQueryMapper implements Processor { 


/*It is assumed the message's body contains an array of query object in Json form
 * 
 * query :{
 * 		term:"<term>",
 * 		score: "<score>"
 * }
 * 
 * This processor will create a request to the EconBiz API( https://api.econbiz.de/doc) out of it
 * 
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
    
    
    String query = "q=";
    JsonNode nodeBuffer;
    
    Iterator<JsonNode> itNodes = rootNode.path("query").getElements();
    String termBuffer = "";
    String scoreBuffer = ""; 
    while ( itNodes.hasNext()){
    	nodeBuffer = itNodes.next();
    	termBuffer = nodeBuffer.path("term").asText();
    	scoreBuffer = nodeBuffer.path("score").asText();
    	
    	query += termBuffer+"^"+scoreBuffer;
    	
    	if ( itNodes.hasNext()){
    		query+="+";
    	}
    
    }
    
    
    in.setHeader(Exchange.HTTP_QUERY,query);
    in.setHeader(Exchange.HTTP_METHOD, "GET");
    
	
	
	
}

}
