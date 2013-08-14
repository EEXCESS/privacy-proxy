package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


public class JSONBody2Properties implements Processor {
	
	JsonFactory jsonFactory = new JsonFactory();
	
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();		
		InputStream input = in.getBody(InputStream.class);
		
		JsonParser jp = jsonFactory.createJsonParser(input);
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {}; 
		HashMap<String,Object> map = mapper.readValue(jp, typeRef); 
		
		for(Entry<String, Object> e: map.entrySet()) {
			exchange.setProperty(e.getKey(), e.getValue());
		}
	}
}
