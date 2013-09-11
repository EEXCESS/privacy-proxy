package eu.eexcess.insa.proxy.connectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.util.TokenBuffer;


public class MendeleyQueriesAggregator implements AggregationStrategy {

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		TokenBuffer buffer;
		if ( oldExchange == null ){
			 buffer = new TokenBuffer (null );
			
			 try {
				buffer.writeStartObject();
				buffer.writeFieldName("documents");
				buffer.writeStartArray();
			} catch (JsonGenerationException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}	 
		}
		else{
			buffer = oldExchange.getIn().getBody(TokenBuffer.class);
		}
		TokenBuffer result;
			
		try {
			result = addDocument(buffer, newExchange);
			newExchange.getIn().setBody(result);
		} catch (JsonParseException e) {
						e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		return newExchange;
	}
	
	
	private TokenBuffer addDocument ( TokenBuffer buffer, Exchange exchange ) throws JsonParseException, IOException{
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);
		if ( is == null ){
			return buffer;
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    JsonNode inputRootNode = mapper.readValue(jp, JsonNode.class);
		
		//buffer.writeFieldName("document");
	    buffer.writeStartObject();
		
		
		if ( !inputRootNode.path("title").isMissingNode()){
			buffer.writeFieldName("title");
			buffer.writeString(inputRootNode.path("title").asText());
		}
		else{
			buffer.writeFieldName("title");
			buffer.writeString("");
		}
		if ( !inputRootNode.path("website").isMissingNode()){
			buffer.writeFieldName("url");
			buffer.writeString(inputRootNode.path("website").asText());
		}
		else{
			buffer.writeFieldName("url");
			buffer.writeString("");
		}
		if ( !inputRootNode.path("type").isMissingNode()){
			buffer.writeFieldName("type");
			buffer.writeString(inputRootNode.path("type").asText());
		}
		else{
			buffer.writeFieldName("type");
			buffer.writeString("");
		}
		if ( !inputRootNode.path("abstract").isMissingNode()){
			buffer.writeFieldName("abstract");
			buffer.writeString(inputRootNode.path("abstract").asText());
		}
		else{
			buffer.writeFieldName("abstract");
			buffer.writeString("");
		}
		
		//buffer.writeFieldName("score");
		//buffer.writeString("");
		buffer.writeFieldName("origin");
		buffer.writeString("Mendeley");
	
		
		
		buffer.writeEndObject();
		return buffer;
	}

}
