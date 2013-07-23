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

public class EconBizQueryMapper implements Processor { 
public int itemsPerPage = 5; //maximum amount of responses we get ftom mendeley

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
	    
	    StringBuffer query = new StringBuffer();
	    JsonNode nodeBuffer;
	    
	    Iterator<JsonNode> itNodes = rootNode.path("query").getElements();
	    String termBuffer = "";
	    String scoreBuffer = "";
	    while ( itNodes.hasNext()){
	    	nodeBuffer = itNodes.next();
	    	termBuffer = nodeBuffer.path("term").asText();
	    	scoreBuffer = nodeBuffer.path("score").asText();
	    	
	    	query.append(termBuffer);
	    	query.append("^");
	    	query.append(scoreBuffer);
	    	
	    	if ( itNodes.hasNext()){
	    		query.append(" OR ");
	    	}
	    }
	    //query.append("&size="+itemsPerPage);
	   String encodedQuery = URLEncoder.encode(query.toString(), "UTF8"); 
	    in.setHeader(Exchange.HTTP_QUERY,"q="+encodedQuery+"&size="+itemsPerPage);
	    in.setHeader(Exchange.HTTP_METHOD, "GET");
	}

}
