package eu.eexcess.insa.proxy.connectors;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class EconBizResultFormater implements Processor { 

	


 
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		InputStream is = in.getBody(InputStream.class);
		
		JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    
	   StringWriter stringWriter = new StringWriter();
	   JsonGenerator jg = factory.createJsonGenerator(stringWriter);
	    JsonNode nodeBuffer;
	    
	    Iterator<JsonNode> itNodes = rootNode.path("hits").path("hits").getElements();
	    String termBuffer = "";
	    String scoreBuffer = "";
	   
	    jg.writeStartObject();
	    jg.writeArrayFieldStart("documents");
	    while ( itNodes.hasNext()){
	    	nodeBuffer = itNodes.next();
	    	jg.writeStartObject();
	    	if( !nodeBuffer.path("title").isMissingNode()){
	    		jg.writeStringField("title",nodeBuffer.path("title").asText());
	    	}
	    	else{
	    		jg.writeStringField("title","");
	    	}
		    if(!nodeBuffer.path("identifier_url").isMissingNode()){
	    		if( !nodeBuffer.path("identifier_url").get(0).isMissingNode()){
		    		jg.writeStringField("url",nodeBuffer.path("identifier_url").get(0).asText());
		    	}
	    		else{
	    			jg.writeStringField("url","");
	    		}
		    }
	    	else{
	    		jg.writeStringField("url","");
	    	}
	    	if( !nodeBuffer.path("type").isMissingNode()){
	    		jg.writeStringField("type",nodeBuffer.path("type").asText());
	    	}
	    	else{
	    		jg.writeStringField("type","");
	    	}
	    	if( !nodeBuffer.path("abstract").isMissingNode()){
		    	if( !nodeBuffer.path("abstract").get(0).isMissingNode()){
		    		jg.writeStringField("abstract",nodeBuffer.path("abstract").get(0).asText());
		    	}
		    	else{
		    		jg.writeStringField("abstract","");
		    	}
	    	}
	    	else{
	    		jg.writeStringField("abstract","");
	    	}
	    	if( !nodeBuffer.path("_score").isMissingNode()){
	    		jg.writeStringField("score",nodeBuffer.path("_score").asText());
	    	}
	    	else{
	    		jg.writeStringField("score","");
	    	}
	    	jg.writeStringField("origin", "EconBiz");
	    	jg.writeEndObject();
	    	
	    	
	    }
	    jg.writeEndArray();
	    jg.writeEndObject();
	    jg.close();
	    in.setBody(stringWriter.toString());
	   
	   
	}

}
