package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class UpdateEEXCESSProfile implements Processor {

	public void process(Exchange exchange) throws Exception {
		
		
Message in = exchange.getIn();
		
		JsonFactory factory = new JsonFactory();
		InputStream profile = in.getBody(InputStream.class);
		JsonParser responseParser = factory.createJsonParser(profile);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(responseParser, JsonNode.class);
		
		
		

	    
	    StringWriter stringWriter = new StringWriter();
	    JsonGenerator jg = factory.createJsonGenerator(stringWriter);
   
	    jg.writeStartObject();
	    jg.writeStringField("source","eexcess");
	    
	 
	    jg.writeFieldName("profile_data");
	    mapper.writeTree(jg, rootNode);
	    
	    
	    
	    jg.writeEndObject();
	    jg.close();
	    
	    in.setBody(stringWriter.toString());
	   // in.setHeader("traceId", exchange.getProperty("user_id",String.class));
	    
	   
	   
	
		
	}


}
