package eu.eexcess.insa.proxy.actions;

import java.io.InputStream;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class PrepareRespLogin implements Processor {
	static JsonFactory factory = new JsonFactory();
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		/*ObjectMapper mapper = new ObjectMapper(factory);
		InputStream is = new StringBufferInputStream(in.getBody(String.class));
		JsonParser parser = factory.createJsonParser(data);
		mapper.readTree(content);
		String reponse = in.getBody(String.class);
		char c = reponse.substring(reponse.indexOf("\"hits\":{\"total\":")+16).charAt(0);
		String takenID = "{\"takenID\": \""+c+"\"}";
		in.setBody(takenID);*/
		
		/*
	    ObjectMapper mapper = new ObjectMapper(factory); 
	   // File from = new File("albumnList.txt"); 
	    TypeReference<HashMap<String,Object>> typeRef 
	          = new TypeReference< 
	                 HashMap<String,Object> 
	               >() {}; 
	    HashMap<String,Object> o 
	         = mapper.readValue(in.getBody(String.class), typeRef); 
	    System.out.println("Got " + o);
	    Object email = o.get("email");
	    System.out.println("email : "+email);
	    if (email != null ){
	    	System.out.println("email : "+email);
	    }
	    */
	    

	    InputStream is = in.getBody(InputStream.class);
	    
	    JsonParser jp = factory.createJsonParser(is);
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode rootNode = mapper.readValue(jp, JsonNode.class);
	    JsonNode entryNode = rootNode.path("hits").path("hits").path(0);
	    
	    String loginValid = rootNode.path("hits").path("total").asText();
	    String id = entryNode.path("_id").asText();
	    System.out.println(id);
	    //String email = entryNode.path("_source").path("email").asText();
	    String username = entryNode.path("_source").path("username").asText();
	    
	    String body = "{\"loginValid\": \""+loginValid+"\",\"id\":\""+id+"\",\"username\":\""+username+"\"}";
	    in.setBody(body);
	}

}
